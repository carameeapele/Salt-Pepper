package com.example.saltpepper.data.authentication.di

import com.example.saltpepper.data.authentication.datasource.AuthenticationRemoteDataSource
import com.example.saltpepper.data.authentication.repository.AuthRepository
import com.example.saltpepper.data.authentication.repository.AuthRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose

private val authenticationScope = named("authenticationScope")

val authenticationDataModule = module {
    single(authenticationScope) { CoroutineScope(SupervisorJob() + Dispatchers.Default) } onClose {
        it?.cancel()
    }
    single { AuthenticationRemoteDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(authenticationScope)) }
}
