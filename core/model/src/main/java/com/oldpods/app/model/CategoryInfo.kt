package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

data class CategoryInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("parent") val parent: Int = 0
)
