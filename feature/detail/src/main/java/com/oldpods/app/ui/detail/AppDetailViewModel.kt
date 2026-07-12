package com.oldpods.app.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oldpods.app.model.AppInfo
import com.oldpods.app.repository.AppRepository
import kotlinx.coroutines.launch

class AppDetailViewModel : ViewModel() {

    private val repository = AppRepository()

    private val _appInfo = MutableLiveData<AppInfo?>()
    val appInfo: LiveData<AppInfo?> = _appInfo

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchAppDetail(appId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getAppDetail(appId)
            result.onSuccess { app ->
                val resolved = repository.resolveImage(app)
                _appInfo.value = resolved
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
