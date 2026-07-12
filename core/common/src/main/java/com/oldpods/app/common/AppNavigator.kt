package com.oldpods.app.common

interface AppNavigator {
    fun navigateToDetail(appId: String)
    fun navigateToSearch(query: String)
    fun navigateToLogin()
}
