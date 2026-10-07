package com.example.saltpepper.data.di

import com.example.saltpepper.data.client.BackendAuthClient
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.data.repositoryImpl.AuthRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import org.koin.dsl.module

val dataModule = module {
    single {
        HttpClient {
            followRedirects = false
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
            }
        }
    }
    single { BackendAuthClient(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}