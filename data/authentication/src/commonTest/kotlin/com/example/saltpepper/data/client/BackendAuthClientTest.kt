package com.example.saltpepper.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BackendAuthClientTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun startPostsEmailToDeployedEndpoint() = runTest {
        val engine = MockEngine { request ->
            assertEquals("https://getsaltpepper.fr/api/auth/email/start", request.url.toString())
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("user@example.com", request.jsonBody()["email"]?.jsonPrimitive?.content)
            respond("""{"message":"Verification code sent"}""", headers = jsonHeaders)
        }
        val httpClient = HttpClient(engine)
        try {
            BackendAuthClient(httpClient).requestEmailCode("user@example.com")
        } finally {
            httpClient.close()
        }
    }

    @Test
    fun verifyPostsEmailAndCodeAndReadsReturnedSession() = runTest {
        val engine = MockEngine { request ->
            assertEquals("https://getsaltpepper.fr/api/auth/email/verify", request.url.toString())
            val body = request.jsonBody()
            assertEquals("user@example.com", body["email"]?.jsonPrimitive?.content)
            assertEquals("527673", body["code"]?.jsonPrimitive?.content)
            respond(
                """{"access_token":"access-123","user":{"id":"user-123","email":"user@example.com"}}""",
                headers = jsonHeaders
            )
        }
        val httpClient = HttpClient(engine)
        try {
            val client = BackendAuthClient(httpClient)
            val user = client.verifyEmailCode("user@example.com", "527673")
            assertEquals("user-123", user.id)
            assertEquals("user@example.com", user.email)
            assertEquals("access-123", client.accessToken)
        } finally {
            httpClient.close()
        }
    }

    @Test
    fun verifyAcceptsSuccessfulResponseWithoutJsonSession() = runTest {
        val engine = MockEngine {
            respond("", HttpStatusCode.NoContent)
        }
        val httpClient = HttpClient(engine)
        try {
            val client = BackendAuthClient(httpClient)
            val user = client.verifyEmailCode("user@example.com", "527673")
            assertEquals("user@example.com", user.id)
            assertEquals("user@example.com", user.email)
            assertNull(client.accessToken)
        } finally {
            httpClient.close()
        }
    }

    @Test
    fun apiErrorIsPropagated() = runTest {
        val engine = MockEngine {
            respond("""{"detail":"Invalid or expired code"}""", HttpStatusCode.BadRequest, jsonHeaders)
        }
        val httpClient = HttpClient(engine)
        try {
            val result = runCatching {
                BackendAuthClient(httpClient).verifyEmailCode("user@example.com", "000000")
            }
            assertEquals("Invalid or expired code", result.exceptionOrNull()?.message)
        } finally {
            httpClient.close()
        }
    }

    private fun HttpRequestData.jsonBody() =
        Json.parseToJsonElement((body as io.ktor.http.content.TextContent).text).jsonObject
}