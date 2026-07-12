package com.oldpods.app.repository

import com.oldpods.app.api.ApiClient
import com.oldpods.app.api.ApiService
import com.oldpods.app.common.Constants
import com.oldpods.app.common.safeApiCall
import com.oldpods.app.model.*

class AppRepository {

    private val apiService: ApiService = ApiClient.getApiService()

    // ========== WordPress REST API ==========

    suspend fun getAppList(page: Int = 1): Result<List<AppInfo>> = safeApiCall {
        apiService.getAppList(page, Constants.DEFAULT_PER_PAGE)
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

    // ========== Weavetech API (POST) ==========

    suspend fun getWeavetechPosts(categoryEndpoint: String, page: Int = 1): Result<WeavetechResponse> {
        return safeApiCall {
            when (categoryEndpoint) {
                "bjshcategory" -> apiService.getBjshPosts(page)
                "ltsjcategory" -> apiService.getLtsjPosts(page)
                "sygjcategory" -> apiService.getSygjPosts(page)
                "tssbcategory" -> apiService.getTssbPosts(page)
                "tsydcategory" -> apiService.getTsydPosts(page)
                "xlbgcategory" -> apiService.getXlbgPosts(page)
                "xxjycategory" -> apiService.getXxjyPosts(page)
                "yxcategory" -> apiService.getYxPosts(page)
                "yystcategory" -> apiService.getYystPosts(page)
                "ztmhcategory" -> apiService.getZtmhPosts(page)
                else -> throw IllegalArgumentException("Unknown endpoint: $categoryEndpoint")
            }
        }
    }
}
