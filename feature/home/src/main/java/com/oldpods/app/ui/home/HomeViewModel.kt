package com.oldpods.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldpods.app.model.AppInfo
import com.oldpods.app.model.ApiMode
import com.oldpods.app.repository.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _appList = MutableLiveData<List<AppInfo>>(emptyList())
    val appList: LiveData<List<AppInfo>> = _appList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 1
    private var hasMoreData = true
    private var isWebMode = true

    fun setApiMode(mode: ApiMode) {
        isWebMode = (mode == ApiMode.WEB)
    }

    fun fetchAppList(page: Int = 1) {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.getAppList(page)
            result.onSuccess { list ->
                if (page == 1) {
                    _appList.value = list
                } else {
                    _appList.value = (_appList.value ?: emptyList()) + list
                }
                currentPage = page
                hasMoreData = list.size >= 15
            }.onFailure {
                _error.value = it.message
            }

            _isLoading.value = false
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value == true || !hasMoreData) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val result = repository.getAppList(currentPage + 1)
            result.onSuccess { list ->
                _appList.value = (_appList.value ?: emptyList()) + list
                currentPage++
                hasMoreData = list.size >= 15
            }
            _isLoadingMore.value = false
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchApps(query)
            result.onSuccess { list ->
                _appList.value = list
                hasMoreData = list.size >= 15
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun loadRandomPost() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getRandomPost()
            result.onSuccess { post ->
                _appList.value = listOf(post)
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
