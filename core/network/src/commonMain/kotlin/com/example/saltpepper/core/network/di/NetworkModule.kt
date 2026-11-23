package com.example.saltpepper.core.network.di

import com.example.saltpepper.core.network.SaltPepperApi
import com.example.saltpepper.core.network.ktor.ApiConfig
import com.example.saltpepper.core.network.ktor.KtorSaltPepperApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import org.koin.dsl.module
import org.koin.dsl.onClose

val networkModule = module {
    single { ApiConfig() }
    single {
        val config = get<ApiConfig>()
        HttpClient {
            followRedirects = false
            install(HttpTimeout) {
                requestTimeoutMillis = config.requestTimeoutMillis
                connectTimeoutMillis = config.connectTimeoutMillis
            }
        }
    } onClose { it?.close() }
    single<SaltPepperApi> { KtorSaltPepperApi(get(), get(), get()) }
}
