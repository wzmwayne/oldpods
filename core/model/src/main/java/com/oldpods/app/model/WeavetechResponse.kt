package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

data class WeavetechResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: WeavetechData? = null
)

data class WeavetechData(
    @SerializedName("apps") val apps: List<WeavetechApp> = emptyList(),
    @SerializedName("pagination") val pagination: WeavetechPagination? = null
)

data class WeavetechApp(
    @SerializedName("id") val id: String? = null,
    @SerializedName("app_name") val appName: String? = null,
    @SerializedName("app_size") val appSize: String? = null,
    @SerializedName("android_version") val androidVersion: String? = null,
    @SerializedName("icon_url") val iconUrl: String? = null,
    @SerializedName("icon_size") val iconSize: String? = null
) {
    fun toAppInfo(): AppInfo {
        return AppInfo(
            id = id?.toIntOrNull() ?: 0,
            title = AppInfo.Title(rendered = appName),
            imageUrl = iconUrl,
            appSize = appSize,
            androidVersion = androidVersion
        )
    }
}

data class WeavetechPagination(
    @SerializedName("current_page") val currentPage: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 15,
    @SerializedName("total") val total: String? = "0",
    @SerializedName("total_pages") val totalPages: Int = 1
) {
    fun getTotalInt(): Int = total?.toIntOrNull() ?: 0
}
