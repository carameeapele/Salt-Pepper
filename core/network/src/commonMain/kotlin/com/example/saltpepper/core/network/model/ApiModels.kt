package com.example.saltpepper.core.network.model

data class UserApiModel(
    val id: String,
    val email: String
)

data class ProfileApiModel(
    val displayName: String?,
    val email: String,
    val createdAt: String
)

data class RecipeApiModel(
    val id: String,
    val title: String,
    val totalTimeMinutes: Int?
)
