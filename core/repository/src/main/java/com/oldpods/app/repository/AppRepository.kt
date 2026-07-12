package com.oldpods.app.repository

import com.oldpods.app.api.ApiClient
import com.oldpods.app.common.safeApiCall
import com.oldpods.app.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class AppRepository {

    private val apiService = ApiClient.getApiService()

    // ========== WordPress REST API ==========

    suspend fun getAppList(page: Int = 1): Result<List<AppInfo>> = safeApiCall {
        apiService.getAppList(page, 15)
    }

    suspend fun getAppDetail(id: String): Result<AppInfo> = safeApiCall {
        apiService.getAppDetail(id)
    }

    suspend fun searchApps(query: String, page: Int = 1): Result<List<AppInfo>> = safeApiCall {
        apiService.searchApps(query, page)
    }

    suspend fun getAppsByCategory(categoryId: Int, page: Int = 1): Result<List<AppInfo>> = safeApiCall {
        apiService.getAppsByCategory(categoryId, page)
    }

    suspend fun getAppsByTopic(topicId: Int, page: Int = 1): Result<List<AppInfo>> = safeApiCall {
        apiService.getAppsByTopic(topicId, page)
    }

    suspend fun getAppsByTag(tagId: Int, page: Int = 1): Result<List<AppInfo>> = safeApiCall {
        apiService.getAppsByTag(tagId, page)
    }

    suspend fun getRandomPost(): Result<AppInfo> = safeApiCall {
        apiService.getRandomPost()
    }

    suspend fun getCategories(): Result<List<CategoryInfo>> = safeApiCall {
        apiService.getCategories()
    }

    suspend fun getTopics(): Result<List<TopicInfo>> = safeApiCall {
        apiService.getTopics()
    }

    suspend fun getTags(page: Int = 1): Result<List<TagInfo>> = safeApiCall {
        apiService.getTags(page = page)
    }

    suspend fun getMediaInfo(id: Int): Result<MediaInfo> = safeApiCall {
        apiService.getMediaInfo(id)
    }

    suspend fun getForumBoards(): Result<List<ForumBoard>> = safeApiCall {
        apiService.getForumBoards()
    }

    suspend fun getComments(postId: Int): Result<List<ForumComment>> = safeApiCall {
        apiService.getComments(postId)
    }

    // ========== Image Resolution ==========

    suspend fun resolveImages(apps: List<AppInfo>): List<AppInfo> = coroutineScope {
        apps.map { app ->
            async {
                if (app.featuredMedia > 0 && app.imageUrl == null) {
                    try {
                        val media = apiService.getMediaInfo(app.featuredMedia)
                        if (media.isSuccessful) {
                            app.imageUrl = media.body()?.sourceUrl
                        }
                    } catch (_: Exception) {}
                }
                app
            }
        }.awaitAll()
    }

    suspend fun resolveImage(app: AppInfo): AppInfo {
        if (app.featuredMedia > 0 && app.imageUrl == null) {
            try {
                val media = apiService.getMediaInfo(app.featuredMedia)
                if (media.isSuccessful) {
                    app.imageUrl = media.body()?.sourceUrl
                }
            } catch (_: Exception) {}
        }
        return app
    }
}
