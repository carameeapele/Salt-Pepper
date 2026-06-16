package com.example.cuisinonsensemble.data.di

import com.example.cuisinonsensemble.data.client.createSupabaseClientInstance
import com.example.cuisinonsensemble.data.repository.AuthRepository
import com.example.cuisinonsensemble.data.repositoryImpl.AuthRepositoryImpl
import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module

val dataModule = module {
    single<SupabaseClient> { createSupabaseClientInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}