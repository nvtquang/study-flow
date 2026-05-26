package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.StudyGroup

data class GroupsUiState(
    val groups: List<StudyGroup> = emptyList(),
    val currentUserId: String = "",
    val query: String = "",
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val activeGroupActionId: String? = null,
    val errorMessage: String? = null,
    val message: String? = null
) {
    val filteredGroups: List<StudyGroup>
        get() = if (query.isBlank()) {
            groups
        } else {
            groups.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
}
