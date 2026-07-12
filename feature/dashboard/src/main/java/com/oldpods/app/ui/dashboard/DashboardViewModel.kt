package com.oldpods.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldpods.app.model.AppInfo
import com.oldpods.app.model.CategoryMapping
import com.oldpods.app.model.CategoryInfo
import com.oldpods.app.repository.AppRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _categoryApps = MutableLiveData<List<AppInfo>>(emptyList())
    val categoryApps: LiveData<List<AppInfo>> = _categoryApps

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _wpCategories = MutableLiveData<List<CategoryInfo>>(emptyList())
    val wpCategories: LiveData<List<CategoryInfo>> = _wpCategories

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentCategoryId: Int = 0

    init {
        fetchWpCategories()
    }

    private fun fetchWpCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            result.onSuccess { cats ->
                _wpCategories.value = cats
            }
        }
    }

    fun loadCategoryApps(category: CategoryMapping.CategoryDef) {
        currentCategoryId = category.wpCategoryId
        if (category.wpCategoryId > 0) {
            loadAppsByWpCategory(category.wpCategoryId)
        } else {
            // wpCategoryId not yet mapped, try to find from fetched categories
            val wpCat = _wpCategories.value?.find {
                it.name?.contains(category.name) == true ||
                category.name.contains(it.name ?: "")
            }
            if (wpCat != null && wpCat.id > 0) {
                loadAppsByWpCategory(wpCat.id)
            } else {
                _categoryApps.value = emptyList()
                _error.value = "未找到分类「${category.name}」"
            }
        }
    }

    private fun loadAppsByWpCategory(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAppsByCategory(categoryId)
            result.onSuccess { apps ->
                // Resolve images in parallel
                val resolved = repository.resolveImages(apps)
                _categoryApps.value = resolved
            }.onFailure {
                _categoryApps.value = emptyList()
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
