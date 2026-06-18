package com.example.cuisinonsensemble.data.client

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

fun createSupabaseClientInstance(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = "https://usffbfvmvtbfsdvjvjex.supabase.co/",
        supabaseKey = "sb_publishable_CNJxMYZ172MIE0iQ8ueuDQ_3x18oCPA",
    ) {
        install(Auth)
    }
}