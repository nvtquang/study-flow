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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.AiMessage
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.AiAssistantUiState

@Composable
fun AiAssistantScreen(
    state: AiAssistantUiState,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.isTyping) {
        val target = state.messages.size + if (state.isTyping) 1 else 0
        if (target > 0) {
            listState.animateScrollToItem(target - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTint)
    ) {
        AiHeader()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { message ->
                AiMessageBubble(message = message)
            }
            if (state.isTyping) {
                item {
                    TypingBubble()
                }
            }
            state.errorMessage?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        AiInputBar(
            value = state.input,
            isTyping = state.isTyping,
            onValueChange = onInputChange,
            onSendClick = onSendClick
        )
    }
}

@Composable
private fun AiHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("StudyFlow", style = MaterialTheme.typography.titleMedium, color = SkyBlue, fontWeight = FontWeight.Bold)
        Text("Trợ lý AI học tập", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Hỏi AI về tài liệu, bài học hoặc kế hoạch ôn tập của bạn.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AiMessageBubble(message: AiMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.84f)
                .background(
                    color = if (message.fromUser) SkyBlue else CardWhite,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (message.fromUser) "Bạn" else "StudyFlow AI",
                color = if (message.fromUser) Color.White else SkyBlue,
                fontWeight = FontWeight.Bold
            )
            message.text.lines().forEach { line ->
                Text(
                    text = line,
                    color = if (message.fromUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun TypingBubble() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .background(CardWhite, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "StudyFlow AI đang trả lời...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AiInputBar(
    value: String,
    isTyping: Boolean,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(SoftLavender, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("+", color = SkyBlue, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Hỏi AI về tài liệu của bạn") },
            singleLine = false,
            maxLines = 3,
            shape = RoundedCornerShape(18.dp)
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (isTyping) SoftLavender else SkyBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = onSendClick, enabled = !isTyping) {
                Text("Gửi", color = if (isTyping) SkyBlue else Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
