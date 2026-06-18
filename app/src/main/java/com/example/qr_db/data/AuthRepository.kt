package com.example.qr_db.data

import retrofit2.Response

class AuthRepository(private val api: QrDbApi) {

    suspend fun login(email: String, password: String): Response<User> {
        return api.login(LoginRequest(email, password))
    }
}