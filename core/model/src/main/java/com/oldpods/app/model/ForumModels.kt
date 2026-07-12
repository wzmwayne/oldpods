package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

data class ForumBoard(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("parent") val parent: Int = 0
)

data class ForumComment(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("post") val post: Int = 0,
    @SerializedName("parent") val parent: Int = 0,
    @SerializedName("author") val author: Int = 0,
    @SerializedName("author_name") val authorName: String? = null,
    @SerializedName("author_email") val authorEmail: String? = null,
    @SerializedName("content") val content: CommentContent? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("status") val status: String? = null
) {
    data class CommentContent(
        @SerializedName("rendered") val rendered: String? = null
    )

    fun getContentText(): String = content?.rendered ?: ""
}
