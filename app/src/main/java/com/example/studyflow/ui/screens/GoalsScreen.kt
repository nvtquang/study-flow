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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.StudyGoal
import com.example.studyflow.ui.components.EmptyStateCard
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.GoalFilter
import com.example.studyflow.viewmodel.GoalsUiState

@Composable
fun GoalsScreen(
    state: GoalsUiState,
    onBackClick: () -> Unit,
    onFilterSelected: (GoalFilter) -> Unit,
    onAddGoal: (String, String, String, String, Boolean) -> Unit,
    onToggleGoal: (StudyGoal) -> Unit,
    onDeleteGoal: (StudyGoal) -> Unit,
    onRetry: () -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = SkyBlue,
                contentColor = Color.White
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = SurfaceTint
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GoalsHeader(onBackClick = onBackClick)
            }
            item {
                GoalsOverviewCard(state = state)
            }
            item {
                GoalsSegmentedTabs(
                    selected = state.selectedFilter,
                    onSelected = onFilterSelected
                )
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
                        Text("Không thể tải mục tiêu", fontWeight = FontWeight.Bold)
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

            if (state.visibleGoals.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = if (state.selectedFilter == GoalFilter.Active) {
                            "Chưa có mục tiêu đang thực hiện"
                        } else {
                            "Chưa có mục tiêu hoàn thành"
                        },
                        message = "Bấm nút + để thêm mục tiêu học tập đầu tiên."
                    )
                }
            } else {
                items(state.visibleGoals) { goal ->
                    GoalListCard(
                        goal = goal,
                        onToggle = { onToggleGoal(goal) },
                        onDelete = { onDeleteGoal(goal) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            isSaving = state.isSaving,
            onDismiss = { showAddDialog = false },
            onSave = { title, description, category, dueDate, isDaily ->
                onAddGoal(title, description, category, dueDate, isDaily)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GoalsHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBackClick) {
            Text("<")
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mục tiêu của tôi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Theo dõi hành trình học tập từng bước.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GoalsOverviewCard(state: GoalsUiState) {
    StudyCard {
        Text(
            text = "Tổng quan hành trình",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tiến độ tổng thể",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${state.completedCount}/${state.totalCount} mục tiêu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${(state.progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                color = SkyBlue,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp)),
            color = SkyBlue,
            trackColor = SoftLavender
        )
    }
}

@Composable
private fun GoalsSegmentedTabs(
    selected: GoalFilter,
    onSelected: (GoalFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite, RoundedCornerShape(20.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GoalFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            TextButton(
                onClick = { onSelected(filter) },
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) SkyBlue else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = filter.label,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GoalListCard(
    goal: StudyGoal,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    StudyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(Lavender.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = goal.category,
                    color = Lavender,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onDelete) {
                Text("Xóa", color = MaterialTheme.colorScheme.error)
            }
        }
        Row(verticalAlignment = Alignment.Top) {
            Checkbox(checked = goal.completed, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (goal.description.isNotBlank()) {
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (goal.isDaily) "Hằng ngày" else "Hạn: ${goal.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Học tập") }
    var dueDate by remember { mutableStateOf("") }
    var isDaily by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Thêm mục tiêu", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DialogField(value = title, onValueChange = { title = it }, label = "Tên mục tiêu")
                DialogField(value = description, onValueChange = { description = it }, label = "Mô tả")
                DialogField(value = category, onValueChange = { category = it }, label = "Danh mục")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mục tiêu hằng ngày", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Bật nếu không có hạn cụ thể",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = isDaily, onCheckedChange = { isDaily = it })
                }
                if (!isDaily) {
                    DialogField(value = dueDate, onValueChange = { dueDate = it }, label = "Hạn hoàn thành")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, description, category, dueDate, isDaily) },
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Đang lưu..." else "Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun DialogField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkyBlue,
            unfocusedBorderColor = StudyBorder,
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = CardWhite
        )
    )
}
