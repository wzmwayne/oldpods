package com.oldpods.app.repository

import com.oldpods.app.api.ApiClient
import com.oldpods.app.common.Constants
import com.oldpods.app.common.safeApiCall
import com.oldpods.app.model.GitHubRelease

class UpdateRepository {

    private val githubService = ApiClient.getGithubService()

    suspend fun getLatestRelease(): Result<GitHubRelease> = safeApiCall {
        githubService.getLatestRelease(Constants.GITHUB_REPO_OWNER, Constants.GITHUB_REPO_NAME)
    }
}
