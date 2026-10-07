package com.example.saltpepper

import android.app.Application
import com.example.saltpepper.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}