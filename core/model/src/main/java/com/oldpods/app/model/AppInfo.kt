package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

data class AppInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: Title? = null,
    @SerializedName("content") val content: Content? = null,
    @SerializedName("excerpt") val excerpt: Excerpt? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("featured_media") val featuredMedia: Int = 0,
    @SerializedName("link") val link: String? = null,
    @SerializedName("categories") val categories: List<Int> = emptyList(),
    @SerializedName("tags") val tags: List<Any> = emptyList(),
    @SerializedName("topics") val topics: List<Any> = emptyList(),
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("modified") val modified: String? = null,
    @SerializedName("format") val format: String? = null,
    @SerializedName("comment_status") val commentStatus: String? = null,
    @SerializedName("ping_status") val pingStatus: String? = null,
    @SerializedName("sticky") val sticky: Boolean = false,
    @SerializedName("_links") val links: Links? = null,
    // Non-serialized fields set manually
    var imageUrl: String? = null,
    var appSize: String? = null,
    var androidVersion: String? = null
) {
    data class Title(
        @SerializedName("rendered") val rendered: String? = null
    )

    data class Content(
        @SerializedName("rendered") val rendered: String? = null
    )

    data class Excerpt(
        @SerializedName("rendered") val rendered: String? = null
    )

    data class Links(
        @SerializedName("self") val self: List<SelfLink>? = null,
        @SerializedName("collection") val collection: List<SelfLink>? = null,
        @SerializedName("author") val author: List<SelfLink>? = null,
        @SerializedName("replies") val replies: List<SelfLink>? = null,
        @SerializedName("wp:attachment") val attachment: List<SelfLink>? = null,
        @SerializedName("wp:term") val term: List<SelfLink>? = null
    )

    data class SelfLink(
        @SerializedName("href") val href: String? = null
    )

    fun getTitleText(): String = title?.rendered ?: ""
    fun getContentText(): String = content?.rendered ?: ""
    fun getExcerptText(): String = excerpt?.rendered ?: ""
}
