package com.example.locationsharingapp.model

data class AppUser(
    val userId: String = "",
    val userEmail: String = "",
    val displayName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)