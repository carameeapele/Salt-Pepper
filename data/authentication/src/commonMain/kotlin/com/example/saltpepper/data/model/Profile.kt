package com.example.saltpepper.data.model

data class MemberProfile(
    val displayName: String?,
    val email: String,
    val memberSince: String
)

data class ProfileRecipe(
    val id: String,
    val title: String,
    val totalTimeMinutes: Int?
)
