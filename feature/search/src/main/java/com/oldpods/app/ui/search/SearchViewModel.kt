package com.oldpods.app.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldpods.app.model.AppInfo
import com.oldpods.app.repository.AppRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _searchResults = MutableLiveData<List<AppInfo>>(emptyList())
    val searchResults: LiveData<List<AppInfo>> = _searchResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.searchApps(query)
            result.onSuccess { apps ->
                val resolved = repository.resolveImages(apps)
                _searchResults.value = resolved
            }.onFailure {
                _searchResults.value = emptyList()
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
