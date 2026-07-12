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

    private val _appInfo = MutableLiveData<AppInfo>()
    val appInfo: LiveData<AppInfo> = _appInfo

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchAppDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAppDetail(id)
            result.onSuccess { _appInfo.value = it }
            _isLoading.value = false
        }
    }
}
