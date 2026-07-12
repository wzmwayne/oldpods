package com.oldpods.app.common

import android.content.Context
import android.content.SharedPreferences
import com.oldpods.app.model.ApiMode
import org.json.JSONArray

object PrefsManager {
    private const val PREFS_NAME = "oldpods_prefs"

    // Keys
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_API_MODE = "api_mode"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NICKNAME = "user_nickname"
    private const val KEY_USER_AVATAR = "user_avatar"
    private const val KEY_UUID = "uuid"
    private const val KEY_GENDER = "gender"
    private const val KEY_LEVEL = "level"
    private const val KEY_LEVEL_INTEGRAL = "level_integral"
    private const val KEY_VIP_LEVEL = "vip_level"
    private const val KEY_VIP_EXP_DATE = "vip_exp_date"
    private const val KEY_DESCRIPTION = "description"
    private const val KEY_SEARCH_HISTORY = "search_history"
    private const val MAX_SEARCH_HISTORY = 10

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(context: Context): Boolean = getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    fun setLoggedIn(context: Context, value: Boolean) = getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    fun getApiMode(context: Context): ApiMode {
        val name = getPrefs(context).getString(KEY_API_MODE, ApiMode.WEB.name) ?: ApiMode.WEB.name
        return try { ApiMode.valueOf(name) } catch (e: Exception) { ApiMode.WEB }
    }
    fun setApiMode(context: Context, mode: ApiMode) = getPrefs(context).edit().putString(KEY_API_MODE, mode.name).apply()

    fun getUserId(context: Context): String = getPrefs(context).getString(KEY_USER_ID, "") ?: ""
    fun setUserId(context: Context, value: String) = getPrefs(context).edit().putString(KEY_USER_ID, value).apply()

    fun getUserEmail(context: Context): String = getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    fun setUserEmail(context: Context, value: String) = getPrefs(context).edit().putString(KEY_USER_EMAIL, value).apply()

    fun getUserNickname(context: Context): String = getPrefs(context).getString(KEY_USER_NICKNAME, "") ?: ""
    fun setUserNickname(context: Context, value: String) = getPrefs(context).edit().putString(KEY_USER_NICKNAME, value).apply()

    fun getUserAvatar(context: Context): String = getPrefs(context).getString(KEY_USER_AVATAR, "") ?: ""
    fun setUserAvatar(context: Context, value: String) = getPrefs(context).edit().putString(KEY_USER_AVATAR, value).apply()

    fun getUuid(context: Context): String = getPrefs(context).getString(KEY_UUID, "") ?: ""
    fun setUuid(context: Context, value: String) = getPrefs(context).edit().putString(KEY_UUID, value).apply()

    fun getGender(context: Context): String = getPrefs(context).getString(KEY_GENDER, "") ?: ""
    fun setGender(context: Context, value: String) = getPrefs(context).edit().putString(KEY_GENDER, value).apply()

    fun getLevel(context: Context): String = getPrefs(context).getString(KEY_LEVEL, "") ?: ""
    fun setLevel(context: Context, value: String) = getPrefs(context).edit().putString(KEY_LEVEL, value).apply()

    fun getLevelIntegral(context: Context): String = getPrefs(context).getString(KEY_LEVEL_INTEGRAL, "") ?: ""
    fun setLevelIntegral(context: Context, value: String) = getPrefs(context).edit().putString(KEY_LEVEL_INTEGRAL, value).apply()

    fun getVipLevel(context: Context): String = getPrefs(context).getString(KEY_VIP_LEVEL, "") ?: ""
    fun setVipLevel(context: Context, value: String) = getPrefs(context).edit().putString(KEY_VIP_LEVEL, value).apply()

    fun getVipExpDate(context: Context): String = getPrefs(context).getString(KEY_VIP_EXP_DATE, "") ?: ""
    fun setVipExpDate(context: Context, value: String) = getPrefs(context).edit().putString(KEY_VIP_EXP_DATE, value).apply()

    fun getDescription(context: Context): String = getPrefs(context).getString(KEY_DESCRIPTION, "") ?: ""
    fun setDescription(context: Context, value: String) = getPrefs(context).edit().putString(KEY_DESCRIPTION, value).apply()

    fun getSearchHistory(context: Context): List<String> {
        val json = getPrefs(context).getString(KEY_SEARCH_HISTORY, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { array.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addSearchHistory(context: Context, query: String) {
        if (query.isBlank()) return
        val history = getSearchHistory(context).toMutableList()
        history.remove(query)
        history.add(0, query)
        val trimmed = if (history.size > MAX_SEARCH_HISTORY) history.subList(0, MAX_SEARCH_HISTORY) else history
        val array = JSONArray(trimmed)
        getPrefs(context).edit().putString(KEY_SEARCH_HISTORY, array.toString()).apply()
    }

    fun clearSearchHistory(context: Context) {
        getPrefs(context).edit().remove(KEY_SEARCH_HISTORY).apply()
    }

    fun saveUserInfo(context: Context, userId: String, email: String, uuid: String) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_UUID, uuid)
            apply()
        }
    }

    fun clearUserInfo(context: Context) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            putString(KEY_USER_ID, "")
            putString(KEY_USER_EMAIL, "")
            putString(KEY_USER_NICKNAME, "")
            putString(KEY_USER_AVATAR, "")
            putString(KEY_UUID, "")
            putString(KEY_GENDER, "")
            putString(KEY_LEVEL, "")
            putString(KEY_LEVEL_INTEGRAL, "")
            putString(KEY_VIP_LEVEL, "")
            putString(KEY_VIP_EXP_DATE, "")
            putString(KEY_DESCRIPTION, "")
            apply()
        }
    }
}
