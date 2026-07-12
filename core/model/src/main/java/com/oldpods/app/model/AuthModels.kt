package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

data class SendCodeRequest(
    @SerializedName("user_email") val email: String
)

data class SendCodeResponse(
    @SerializedName("code") val code: Int = 0,
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("verification_code") val verificationCode: String? = null
) {
    fun isSuccess(): Boolean = code == 1
}

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class SilentLoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("uuid") val uuid: String
)

data class LoginResponse(
    @SerializedName("code") val code: Int = 0,
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("email") val email: String? = null
) {
    fun isSuccess(): Boolean = code == 1
}

data class UserMetaRequest(
    @SerializedName("email") val email: String,
    @SerializedName("mail_uuid") val mailUuid: String
)

data class UserMetaResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: UserData? = null
) {
    fun isSuccess(): Boolean = status == "success"

    data class UserData(
        @SerializedName("nickname") val nickname: String? = null,
        @SerializedName("gender") val gender: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("level") val level: String? = null,
        @SerializedName("level_integral") val levelIntegral: String? = null,
        @SerializedName("points") val points: String? = null,
        @SerializedName("auth") val auth: String? = null,
        @SerializedName("vip_level") val vipLevel: String? = null,
        @SerializedName("vip_exp_date") val vipExpDate: String? = null
    )
}
