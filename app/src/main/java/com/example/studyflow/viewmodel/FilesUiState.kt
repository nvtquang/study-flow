package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.StudyFile

enum class FileSortOption(val label: String) {
    Recent("Gần đây"),
    Name("Tên"),
    Size("Kích thước")
}

data class FilesUiState(
    val files: List<StudyFile> = emptyList(),
    val query: String = "",
    val sortOption: FileSortOption = FileSortOption.Recent,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
) {
    val filteredFiles: List<StudyFile>
        get() {
            val searched = if (query.isBlank()) {
                files
            } else {
                files.filter { it.name.contains(query.trim(), ignoreCase = true) }
            }

            return when (sortOption) {
                FileSortOption.Recent -> searched.sortedByDescending { it.createdAt }
                FileSortOption.Name -> searched.sortedBy { it.name.lowercase() }
                FileSortOption.Size -> searched.sortedByDescending { it.sizeBytes }
            }
        }

    val totalBytes: Long
        get() = files.sumOf { it.sizeBytes }
}
