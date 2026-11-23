package com.example.saltpepper.core.session.di

import com.example.saltpepper.core.session.PersistentSession
import com.example.saltpepper.core.session.SessionManager
import com.example.saltpepper.core.session.createSecureSessionStore
import org.koin.dsl.module

val sessionModule = module {
    single { createSecureSessionStore() }
    single { PersistentSession(get()) }
    single { SessionManager(get()) }
}
