package com.example.pitabmdmstudent.data.remote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.data.remote.repository.StudentRepository
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

//    fun sendDeviceState(request: DeviceStateRequest) {
//        viewModelScope.launch {
//            val success = repository.updateDeviceState(request)
//            Log.d("DeviceState", "Uploaded = $success")
//        }
//    }
}