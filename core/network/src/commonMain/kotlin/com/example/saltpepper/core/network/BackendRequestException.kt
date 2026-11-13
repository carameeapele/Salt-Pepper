package com.example.saltpepper.core.network

class BackendRequestException(val status: Int, message: String) : Exception(message)
