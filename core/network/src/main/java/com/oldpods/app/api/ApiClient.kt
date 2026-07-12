package com.oldpods.app.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val MAIN_BASE_URL = "https://oldpods.cn/"
    const val GITHUB_BASE_URL = "https://api.github.com/"

    private var mainRetrofit: Retrofit? = null
    private var githubRetrofit: Retrofit? = null
    private var apiService: ApiService? = null
    private var githubService: GithubService? = null

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(logging)
            .build()
    }

    fun getApiService(): ApiService {
        if (apiService == null) {
            mainRetrofit = Retrofit.Builder()
                .baseUrl(MAIN_BASE_URL)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = mainRetrofit!!.create(ApiService::class.java)
        }
        return apiService!!
    }

    fun getGithubService(): GithubService {
        if (githubService == null) {
            githubRetrofit = Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            githubService = githubRetrofit!!.create(GithubService::class.java)
        }
        return githubService!!
    }
}
