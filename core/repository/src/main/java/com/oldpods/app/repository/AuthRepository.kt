package com.oldpods.app.repository

import com.oldpods.app.api.ApiClient
import com.oldpods.app.common.safeApiCall
import com.oldpods.app.model.*

class AuthRepository {

    private val apiService = ApiClient.getApiService()

    suspend fun sendVerificationCode(email: String): Result<SendCodeResponse> = safeApiCall {
        apiService.sendVerificationCode(email)
    }

    suspend fun loginWithCode(email: String, code: String): Result<LoginResponse> = safeApiCall {
        apiService.loginWithCode(LoginRequest(email, code))
    }

    suspend fun silentLogin(email: String, uuid: String): Result<LoginResponse> = safeApiCall {
        apiService.silentLogin(SilentLoginRequest(email, uuid))
    }

    suspend fun getUserMeta(email: String, mailUuid: String): Result<UserMetaResponse> = safeApiCall {
        apiService.getUserMeta(UserMetaRequest(email, mailUuid))
    }
}
