package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.StudyGroup
import com.example.studyflow.ui.components.Avatar
import com.example.studyflow.ui.components.EmptyStateCard
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.SectionTitle
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.components.StudySearchBar
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.GroupsUiState

@Composable
fun GroupsScreen(
    state: GroupsUiState,
    onSearchChange: (String) -> Unit,
    onCreateGroup: (String, String) -> Unit,
    onGroupClick: (StudyGroup) -> Unit,
    onRetry: () -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTint)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { GroupsHeader() }
            item {
                StudySearchBar(
                    value = state.query,
                    onValueChange = onSearchChange,
                    placeholder = "Tìm kiếm nhóm học tập..."
                )
            }
            item {
                PrimaryButton(
                    text = if (state.isCreating) "Đang tạo..." else "+ Tạo nhóm mới",
                    onClick = { showCreateDialog = true }
                )
            }
            item {
                SectionTitle(title = "Nhóm học tập của tôi", actionText = "Đã tham gia")
            }

            if (state.isLoading) {
                item {
                    StudyCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SkyBlue)
                        }
                    }
                }
                return@LazyColumn
            }

            if (state.errorMessage != null) {
                item {
                    StudyCard {
                        Text("Không thể tải nhóm", fontWeight = FontWeight.Bold)
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = onRetry) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            if (state.filteredGroups.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Tìm nhóm mới",
                        message = "Tạo nhóm học tập đầu tiên để bắt đầu thảo luận với bạn bè."
                    )
                }
            } else {
                items(state.filteredGroups) { group ->
                    StudyGroupCard(group = group, onClick = { onGroupClick(group) })
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showCreateDialog) {
        CreateGroupDialog(
            isCreating = state.isCreating,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description ->
                onCreateGroup(name, description)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun GroupsHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("StudyFlow", style = MaterialTheme.typography.titleMedium, color = SkyBlue, fontWeight = FontWeight.Bold)
        Text("Nhóm học tập", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Trao đổi bài học, chia sẻ tài liệu và giữ nhịp cùng nhóm.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StudyGroupCard(
    group: StudyGroup,
    onClick: () -> Unit
) {
    StudyCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(SoftLavender, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(group.subjectIcon, color = SkyBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(group.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    group.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${group.memberIds.size} thành viên",
                style = MaterialTheme.typography.bodySmall,
                color = SkyBlue,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            MemberAvatars(names = group.memberNames)
        }
        Text(
            text = group.lastMessage.ifBlank { "Chưa có tin nhắn" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MemberAvatars(names: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        names.take(4).forEachIndexed { index, name ->
            Avatar(
                initials = name.take(1).ifBlank { "S" },
                background = if (index % 2 == 0) SkyBlue else Lavender
            )
        }
    }
}

@Composable
private fun CreateGroupDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo nhóm mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên nhóm") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    singleLine = false
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name, description) }, enabled = !isCreating) {
                Text(if (isCreating) "Đang tạo..." else "Tạo nhóm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        containerColor = CardWhite
    )
}
