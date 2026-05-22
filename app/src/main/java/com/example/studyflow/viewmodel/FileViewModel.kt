package com.example.studyflow.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.StudyFile
import com.example.studyflow.data.repository.StorageRepository
import com.example.studyflow.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FileViewModel(
    private val storageRepository: StorageRepository = StorageRepository()
) : ViewModel() {
    private val _filesState = MutableStateFlow<UiState<List<StudyFile>>>(UiState.Idle)
    val filesState: StateFlow<UiState<List<StudyFile>>> = _filesState.asStateFlow()

    private val _uploadState = MutableStateFlow<UiState<StudyFile>>(UiState.Idle)
    val uploadState: StateFlow<UiState<StudyFile>> = _uploadState.asStateFlow()

    fun loadFiles(ownerUid: String) {
        _filesState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                storageRepository.getFiles(ownerUid)
            }.onSuccess { files ->
                _filesState.value = if (files.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(files)
                }
            }.onFailure { throwable ->
                _filesState.value = UiState.Error(throwable.message ?: "Unable to load files.")
            }
        }
    }

    fun uploadFile(
        ownerUid: String,
        localUri: Uri,
        fileName: String,
        contentType: String = ""
    ) {
        _uploadState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                storageRepository.uploadStudyFile(
                    ownerUid = ownerUid,
                    localUri = localUri,
                    fileName = fileName,
                    contentType = contentType
                )
            }.onSuccess { file ->
                _uploadState.value = UiState.Success(file)
                loadFiles(ownerUid)
            }.onFailure { throwable ->
                _uploadState.value = UiState.Error(throwable.message ?: "Unable to upload file.")
            }
        }
    }
}
