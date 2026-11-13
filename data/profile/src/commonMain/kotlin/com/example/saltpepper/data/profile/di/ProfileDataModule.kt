package com.example.saltpepper.data.profile.di

import com.example.saltpepper.data.profile.datasource.ProfileRemoteDataSource
import com.example.saltpepper.data.profile.repository.ProfileRepository
import com.example.saltpepper.data.profile.repository.ProfileRepositoryImpl
import org.koin.dsl.module

val profileDataModule = module {
    single { ProfileRemoteDataSource(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
}
