package com.example.saltpepper.data.di

import com.example.saltpepper.data.client.BackendAuthClient
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.data.repositoryImpl.AuthRepositoryImpl
import com.example.saltpepper.data.session.PersistentSession
import com.example.saltpepper.data.session.createSecureSessionStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import org.koin.dsl.module

val dataModule = module {
    single {
        HttpClient {
            followRedirects = false
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
            }
        }
    }
    single { PersistentSession(createSecureSessionStore()) }
    single { BackendAuthClient(get(), session = get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}