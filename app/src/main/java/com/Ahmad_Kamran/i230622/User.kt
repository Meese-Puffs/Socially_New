package com.Ahmad_Kamran.i230622

data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String,

    val profileImage: String? = null,
    val bio: String? = null,
    val followersCount: Int = 0
)