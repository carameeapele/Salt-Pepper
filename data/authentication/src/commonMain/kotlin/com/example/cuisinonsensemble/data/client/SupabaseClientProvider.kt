package com.example.cuisinonsensemble.data.client

import com.example.cuisinonsensemble.data.config.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

fun createSupabaseClientInstance(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = BuildKonfig.SUPABASE_URL,
        supabaseKey = BuildKonfig.SUPABASE_PUBLISHABLE_KEY,
    ) {
        install(Auth)
    }
}