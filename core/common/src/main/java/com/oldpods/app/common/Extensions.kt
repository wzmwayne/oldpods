package com.oldpods.app.common

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun String.parseVersionCode(): Int {
    return try {
        this.replace(Regex("[^0-9.]"), "")
            .split(".")
            .filter { it.isNotEmpty() }
            .fold(0) { acc, part -> acc * 100 + (part.toIntOrNull() ?: 0) }
    } catch (e: Exception) {
        0
    }
}
