package com.example.studyflow.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.StudyFile
import com.example.studyflow.ui.components.EmptyStateCard
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.SectionTitle
import com.example.studyflow.ui.components.StatCard
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.components.StudySearchBar
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.FileSortOption
import com.example.studyflow.viewmodel.FilesUiState

@Composable
fun FilesScreen(
    state: FilesUiState,
    onSearchChange: (String) -> Unit,
    onSortSelected: (FileSortOption) -> Unit,
    onUploadFile: (Uri, String, String) -> Unit,
    onRetry: () -> Unit,
    onMessageShown: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val meta = context.readFileMeta(uri)
            onUploadFile(uri, meta.name, meta.contentType)
        }
    }

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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { FilesHeader() }
            item {
                StudySearchBar(
                    value = state.query,
                    onValueChange = onSearchChange,
                    placeholder = "Tìm kiếm tệp và thư mục..."
                )
            }
            item {
                FileSortChips(
                    selected = state.sortOption,
                    onSelected = onSortSelected
                )
            }
            item {
                StorageOverviewCard(totalBytes = state.totalBytes)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(title = "Thư mục chia sẻ", value = "4", modifier = Modifier.weight(1f))
                    StatCard(title = "Đã đánh dấu", value = "7", modifier = Modifier.weight(1f), accentColor = Lavender)
                }
            }
            item {
                FolderGrid()
            }
            item {
                SectionTitle(title = "Tệp gần đây")
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
                        Text("Không thể tải kho tài liệu", fontWeight = FontWeight.Bold)
                        Text(
                            state.errorMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onRetry) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            if (state.filteredFiles.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "Chưa có tệp",
                        message = "Bấm Tải lên để thêm tài liệu học tập đầu tiên."
                    )
                }
            } else {
                items(state.filteredFiles) { file ->
                    FileRow(
                        file = file,
                        onClick = {
                            context.openFileUrl(file.downloadUrl)
                        }
                    )
                }
            }

            item {
                PrimaryButton(
                    text = if (state.isUploading) "Đang tải lên..." else "Tải lên",
                    onClick = { filePicker.launch(arrayOf("*/*")) }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FilesHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "StudyFlow",
            style = MaterialTheme.typography.titleMedium,
            color = SkyBlue,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Kho tài liệu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Lưu trữ và truy cập tài liệu học tập của bạn.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FileSortChips(
    selected: FileSortOption,
    onSelected: (FileSortOption) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FileSortOption.entries.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) SkyBlue else CardWhite,
                        RoundedCornerShape(999.dp)
                    )
                    .clickable { onSelected(option) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    text = option.label,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StorageOverviewCard(totalBytes: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(SkyBlue, Lavender)),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Tổng quan bộ nhớ",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Đã lưu ${totalBytes.formatBytes()} tài liệu học tập",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun FolderGrid() {
    val folders = listOf("Bài giảng", "Bài tập", "Dự án", "Chia sẻ")

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .size(height = 180.dp, width = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(folders) { folder ->
            StudyCard {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SoftLavender, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("F", color = SkyBlue, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = folder,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FileRow(
    file: StudyFile,
    onClick: () -> Unit
) {
    StudyCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(SoftLavender, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(file.name.extensionLabel(), color = SkyBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${file.contentType.ifBlank { "Tệp" }} • ${file.sizeBytes.formatBytes()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class PickedFileMeta(
    val name: String,
    val contentType: String
)

private fun android.content.Context.readFileMeta(uri: Uri): PickedFileMeta {
    val fallbackName = uri.lastPathSegment?.substringAfterLast('/') ?: "studyflow-file"
    var displayName = fallbackName

    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            displayName = cursor.getString(nameIndex) ?: fallbackName
        }
    }

    return PickedFileMeta(
        name = displayName,
        contentType = contentResolver.getType(uri).orEmpty()
    )
}

private fun android.content.Context.openFileUrl(url: String) {
    if (url.isBlank()) return

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

private fun Long.formatBytes(): String {
    if (this <= 0L) return "0 KB"
    val kb = this / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        "%.1f MB".format(mb)
    } else {
        "%.0f KB".format(kb)
    }
}

private fun String.extensionLabel(): String {
    val extension = substringAfterLast('.', missingDelimiterValue = "F")
    return extension.take(3).uppercase()
}
