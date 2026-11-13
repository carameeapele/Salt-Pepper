package com.example.saltpepper.core.session

interface SecureSessionStore {
    fun read(): String?
    fun write(value: String)
    fun clear()
}

expect fun createSecureSessionStore(): SecureSessionStore