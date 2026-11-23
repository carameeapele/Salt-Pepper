package com.example.saltpepper.di

import com.example.saltpepper.core.network.AppApi
import com.example.saltpepper.core.network.di.networkModule
import com.example.saltpepper.core.session.SecureSessionStore
import com.example.saltpepper.core.session.di.sessionModule
import com.example.saltpepper.data.authentication.di.authenticationDataModule
import com.example.saltpepper.data.authentication.repository.AuthRepository
import com.example.saltpepper.data.authentication.repository.AuthState
import com.example.saltpepper.data.profile.di.profileDataModule
import com.example.saltpepper.data.profile.repository.ProfileRepository
import com.example.saltpepper.data.recipe.di.recipeDataModule
import com.example.saltpepper.data.recipe.repository.RecipeRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DataModulesTest {
    @Test
    fun repositoriesResolveAndShareOneAuthenticatedApiAndSession() = runTest {
        val authenticatedRequests = mutableListOf<String>()
        val http = HttpClient(MockEngine { request ->
            val path = request.url.encodedPath
            if (path == "/api/auth/email/verify") {
                respond(
                    """{"access_token":"shared-access","expires_in":3600,"user":{"id":"1","email":"user@example.test"}}""",
                    headers = headersOf(
                        HttpHeaders.SetCookie, "sp_refresh=shared-refresh; Max-Age=86400; Path=/api/auth"
                    )
                )
            } else {
                assertEquals("Bearer shared-access", request.headers[HttpHeaders.Authorization])
                authenticatedRequests += path
                when (path) {
                    "/api/me" -> respond(
                        """{"display_name":"Camille","email":"user@example.test","created_at":"2026-11-10T00:00:00Z"}"""
                    )
                    "/api/recipes/favorites" -> respond("[]")
                    "/api/me/delete" -> respond("", HttpStatusCode.NoContent)
                    else -> error("Unexpected endpoint: $path")
                }
            }
        })
        val application = koinApplication {
            modules(
                sessionModule, networkModule, authenticationDataModule, profileDataModule, recipeDataModule,
                module {
                    single<SecureSessionStore> {
                        object : SecureSessionStore {
                            private var value: String? = null
                            override fun read() = value
                            override fun write(value: String) { this.value = value }
                            override fun clear() { value = null }
                        }
                    }
                    single { http }
                }
            )
        }
        try {
            val auth = application.koin.get<AuthRepository>()
            val profile = application.koin.get<ProfileRepository>()
            val recipes = application.koin.get<RecipeRepository>()
            assertSame(application.koin.get<AppApi>(), application.koin.get<AppApi>())
            auth.verifyEmailCode("user@example.test", "123456").getOrThrow()
            auth.authState.first { it is AuthState.Authenticated }
            assertEquals("Camille", profile.getProfile().getOrThrow().displayName)
            assertEquals(emptyList(), recipes.getFavorites().getOrThrow())
            profile.deleteAccount("123456", false).getOrThrow()
            auth.authState.first { it == AuthState.Unauthenticated }
            assertEquals(listOf("/api/me", "/api/recipes/favorites", "/api/me/delete"), authenticatedRequests)
        } finally {
            application.close()
            http.close()
        }
    }
}
