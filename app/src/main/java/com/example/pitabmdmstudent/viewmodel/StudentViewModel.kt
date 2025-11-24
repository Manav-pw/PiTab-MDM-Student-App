package com.example.pitabmdmstudent.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.data.repository.StudentRepository
import com.example.pitabmdmstudent.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _uploadStatus = MutableStateFlow<Boolean?>(null)
    val uploadStatus = _uploadStatus.asStateFlow()

    private val _pairingCode = MutableStateFlow<String?>(null)
    val pairingCode = _pairingCode.asStateFlow()

    fun loadPairingCode(onLoaded: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.getPairingCode() ?: ""
            if(result.isNotEmpty()){
                _pairingCode.value = result
                onLoaded(result)
            }
        }
    }

    fun uploadInstalledApps(context: Context) {
        viewModelScope.launch {
            val installed = AppUtils.getInstalledApps(context)
            val result = repository.uploadInstalledApps(installed)
            _uploadStatus.value = result
        }
    }
}
