package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.studyflow.data.model.UserProfile
import com.example.studyflow.ui.components.Avatar
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.SecondaryButton
import com.example.studyflow.ui.components.SectionTitle
import com.example.studyflow.ui.components.StatCard
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.ProfileUiState

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onEditName: (String) -> Unit,
    onGoalsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onRetry: () -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditDialog by remember { mutableStateOf(false) }

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
            item { ProfileHeader() }

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
                        Text("Không thể tải hồ sơ", fontWeight = FontWeight.Bold)
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = onRetry) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            state.profile?.let { profile ->
                item {
                    ProfileCard(profile = profile, onEditClick = { showEditDialog = true })
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Chuỗi hiện tại", "${profile.currentStreak}", Modifier.weight(1f))
                        StatCard("Giờ đã học", profile.totalFocusSeconds.toHoursText(), Modifier.weight(1f), Lavender)
                    }
                }
                item {
                    StatCard("Điểm tập trung", "${profile.focusScore}", Modifier.fillMaxWidth(), SkyBlue)
                }
                item { SectionTitle("Lối tắt") }
                item { SecondaryButton("Mục tiêu của tôi", onGoalsClick) }
                item { SecondaryButton("Nhóm của tôi", onGroupsClick) }
                item { SecondaryButton("Kho tài liệu", onFilesClick) }
                item { SecondaryButton("Cài đặt ứng dụng", onSettingsClick) }
                item {
                    OutlinedButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Đăng xuất", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    val profile = state.profile
    if (showEditDialog && profile != null) {
        EditProfileDialog(
            currentName = profile.displayName,
            isSaving = state.isSaving,
            onDismiss = { showEditDialog = false },
            onSave = { newName ->
                onEditName(newName)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun ProfileHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("StudyFlow", style = MaterialTheme.typography.titleMedium, color = SkyBlue, fontWeight = FontWeight.Bold)
        Text("Hồ sơ cá nhân", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProfileCard(profile: UserProfile, onEditClick: () -> Unit) {
    StudyCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Avatar(initials = profile.displayName.initials(), modifier = Modifier.size(92.dp), background = SkyBlue)
            Text(
                profile.displayName.ifBlank { "StudyFlow User" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(profile.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).background(Color(0xFF22C55E), CircleShape))
                Spacer(Modifier.size(8.dp))
                Text("Online", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
            }
            PrimaryButton("Chỉnh sửa Hồ sơ", onEditClick)
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên hiển thị") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = !isSaving) {
                Text(if (isSaving) "Đang lưu..." else "Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

private fun String.initials(): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return "SF"
    return trimmed.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
}

private fun Long.toHoursText(): String {
    val hours = this / 3600
    return "${hours}h"
}
