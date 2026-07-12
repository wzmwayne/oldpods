package com.oldpods.app.model

import com.google.gson.annotations.SerializedName

data class MediaInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: MediaTitle? = null,
    @SerializedName("media_details") val mediaDetails: MediaDetails? = null,
    @SerializedName("source_url") val sourceUrl: String? = null
) {
    data class MediaTitle(
        @SerializedName("rendered") val rendered: String? = null
    )

    data class MediaDetails(
        @SerializedName("width") val width: Int = 0,
        @SerializedName("height") val height: Int = 0,
        @SerializedName("sizes") val sizes: MediaSizes? = null
    )

    data class MediaSizes(
        @SerializedName("medium") val medium: MediaSize? = null,
        @SerializedName("large") val large: MediaSize? = null,
        @SerializedName("thumbnail") val thumbnail: MediaSize? = null
    )

    data class MediaSize(
        @SerializedName("source_url") val sourceUrl: String? = null,
        @SerializedName("width") val width: Int = 0,
        @SerializedName("height") val height: Int = 0
    )
}
