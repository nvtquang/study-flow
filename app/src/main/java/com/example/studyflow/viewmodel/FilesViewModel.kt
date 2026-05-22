package com.example.studyflow.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilesViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val fileRepository: FileRepository = FileRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(FilesUiState(isLoading = true))
    val uiState: StateFlow<FilesUiState> = _uiState.asStateFlow()

    init {
        loadFiles()
    }

    fun loadFiles() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để xem kho tài liệu.")
                fileRepository.getFiles(userId)
            }.onSuccess { files ->
                _uiState.value = _uiState.value.copy(
                    files = files,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Không thể tải tệp."
                )
            }
        }
    }

    fun updateQuery(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
    }

    fun selectSort(option: FileSortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
    }

    fun uploadFile(uri: Uri, fileName: String, contentType: String) {
        _uiState.value = _uiState.value.copy(isUploading = true, message = null)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để tải tệp lên.")
                fileRepository.uploadFile(
                    userId = userId,
                    localUri = uri,
                    fileName = fileName,
                    contentType = contentType
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    message = "Tải lên thành công."
                )
                loadFiles()
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    message = throwable.message ?: "Không thể tải tệp lên."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
