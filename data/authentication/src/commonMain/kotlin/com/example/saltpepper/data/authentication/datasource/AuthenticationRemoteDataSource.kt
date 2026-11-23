package com.example.saltpepper.data.authentication.datasource

import com.example.saltpepper.core.network.SaltPepperApi

class AuthenticationRemoteDataSource(private val api: SaltPepperApi) {
    suspend fun requestEmailCode(email: String) = api.requestEmailCode(email)
    suspend fun register(email: String, password: String) = api.register(email, password)
    suspend fun verifyEmailCode(email: String, code: String) =
        api.verifyEmailCode(email, code)
    suspend fun verifyRegistrationCode(email: String, code: String) =
        api.verifyRegistrationCode(email, code)
    suspend fun restoreSession() = api.restoreSession()
    suspend fun logout() = api.logout()
}
