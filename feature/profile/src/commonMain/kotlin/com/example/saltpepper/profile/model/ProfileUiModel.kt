package com.example.saltpepper.profile.model

import com.example.saltpepper.data.profile.model.MemberProfile

data class ProfileUiModel(
    val displayName: String,
    val email: String,
    val memberSince: String
)

fun MemberProfile.toUiModel() = ProfileUiModel(
    displayName = displayName?.takeIf { it.isNotBlank() } ?: email,
    email = email,
    memberSince = memberSince
)
