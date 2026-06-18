package com.example.cuisinonsensemble

import android.app.Application
import com.example.cuisinonsensemble.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}