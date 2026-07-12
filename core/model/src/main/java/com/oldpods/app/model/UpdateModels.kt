package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

// GitHub Releases API
data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("published_at") val publishedAt: String? = null,
    @SerializedName("body") val body: String? = null,
    @SerializedName("assets") val assets: List<GitHubAsset> = emptyList()
) {
    data class GitHubAsset(
        @SerializedName("name") val name: String? = null,
        @SerializedName("browser_download_url") val downloadUrl: String? = null,
        @SerializedName("size") val size: Long = 0,
        @SerializedName("content_type") val contentType: String? = null
    )

    fun getVersionName(): String = tagName?.removePrefix("v") ?: name ?: ""
    fun getApkDownloadUrl(): String? =
        assets.firstOrNull { it.name?.endsWith(".apk") == true }?.downloadUrl
}

// Old update server (deprecated)
data class OldUpdateInfo(
    @SerializedName("code") val code: Int = 0,
    @SerializedName("msg") val message: String? = null,
    @SerializedName("data") val data: List<OldUpdateData>? = null
) {
    data class OldUpdateData(
        @SerializedName("UpdateNotes") val updateNotes: String? = null,
        @SerializedName("version") val version: String? = null,
        @SerializedName("url") val url: String? = null
    )

    fun hasUpdate(): Boolean = code == 1 && !data.isNullOrEmpty()
    fun getLatestUpdate(): OldUpdateData? = data?.firstOrNull()
}
