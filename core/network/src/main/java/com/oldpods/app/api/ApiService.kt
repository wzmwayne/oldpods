package com.oldpods.app.api

import com.oldpods.app.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== WordPress REST API (Base: https://oldpods.cn/) ==========

    @GET("wp-json/wp/v2/posts")
    suspend fun getAppList(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<List<AppInfo>>

    @GET("wp-json/wp/v2/posts/{id}")
    suspend fun getAppDetail(@Path("id") id: String): Response<AppInfo>

    @GET("wp-json/wp/v2/posts")
    suspend fun searchApps(
        @Query("search") search: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<List<AppInfo>>

    @GET("wp-json/wp/v2/posts")
    suspend fun getAppsByCategory(
        @Query("categories[]") categoryId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<List<AppInfo>>

    @GET("wp-json/wp/v2/posts")
    suspend fun getAppsByTopic(
        @Query("topics") topicId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<List<AppInfo>>

    @GET("wp-json/wp/v2/posts")
    suspend fun getAppsByTag(
        @Query("tags") tagId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<List<AppInfo>>

    @GET("wp-json/wp/v2/random-post")
    suspend fun getRandomPost(): Response<AppInfo>

    @GET("wp-json/wp/v2/categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 100
    ): Response<List<CategoryInfo>>

    @GET("wp-json/wp/v2/topics")
    suspend fun getTopics(
        @Query("per_page") perPage: Int = 50
    ): Response<List<TopicInfo>>

    @GET("wp-json/wp/v2/tags")
    suspend fun getTags(
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): Response<List<TagInfo>>

    @GET("wp-json/wp/v2/media/{id}")
    suspend fun getMediaInfo(@Path("id") id: Int): Response<MediaInfo>

    // Forum
    @GET("wp-json/wp/v2/plate_cat")
    suspend fun getForumBoards(
        @Query("per_page") perPage: Int = 50
    ): Response<List<ForumBoard>>

    @GET("wp-json/wp/v2/comments")
    suspend fun getComments(
        @Query("post") postId: Int,
        @Query("per_page") perPage: Int = 50
    ): Response<List<ForumComment>>

    // ========== Auth (Weavetech - kept for login) ==========

    @POST("weavetech/user/usermail/index.php")
    @FormUrlEncoded
    suspend fun sendVerificationCode(
        @Field("user_email") email: String
    ): Response<SendCodeResponse>

    @POST("weavetech/user/userlogin/index.php")
    suspend fun loginWithCode(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("weavetech/user/userlogin/index.php")
    suspend fun silentLogin(
        @Body request: SilentLoginRequest
    ): Response<LoginResponse>

    @POST("weavetech/user/usermeta/index.php")
    suspend fun getUserMeta(
        @Body request: UserMetaRequest
    ): Response<UserMetaResponse>
}
