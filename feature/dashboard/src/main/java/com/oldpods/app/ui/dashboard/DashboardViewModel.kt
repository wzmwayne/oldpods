package com.oldpods.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldpods.app.model.ApiMode
import com.oldpods.app.model.AppInfo
import com.oldpods.app.model.CategoryMapping
import com.oldpods.app.model.WeavetechApp
import com.oldpods.app.repository.AppRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _categoryApps = MutableLiveData<List<AppInfo>>(emptyList())
    val categoryApps: LiveData<List<AppInfo>> = _categoryApps

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var isWebMode = true

    fun setApiMode(mode: ApiMode) {
        isWebMode = (mode == ApiMode.WEB)
    }

    fun loadCategoryApps(category: CategoryMapping.CategoryDef) {
        viewModelScope.launch {
            _isLoading.value = true
            if (isWebMode) {
                // WordPress: use posts with categories filter
                // The CategoryMapping.wpCategoryId is 0, so we need to fetch categories first
                // For now, fallback to Weavetech
                loadFromWeavetech(category)
            } else {
                loadFromWeavetech(category)
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadFromWeavetech(category: CategoryMapping.CategoryDef) {
        val result = repository.getWeavetechPosts(category.endpoint)
        result.onSuccess { response ->
            val apps = response.data?.apps?.map { it.toAppInfo() } ?: emptyList()
            _categoryApps.value = apps
        }.onFailure {
            _categoryApps.value = emptyList()
        }
    }
}
