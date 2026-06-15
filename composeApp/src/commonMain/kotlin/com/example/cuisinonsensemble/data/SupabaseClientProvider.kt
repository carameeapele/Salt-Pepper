package com.example.cuisinonsensemble.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://usffbfvmvtbfsdvjvjex.supabase.co/",
        supabaseKey = "sb_publishable_CNJxMYZ172MIE0iQ8ueuDQ_3x18oCPA",
    ) {
        install(Auth)
    }
}