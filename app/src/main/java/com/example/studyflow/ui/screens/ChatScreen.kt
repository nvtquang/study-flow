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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.ChatMessage
import com.example.studyflow.ui.components.Avatar
import com.example.studyflow.ui.components.MessageBubble
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.ChatUiState

@Composable
fun ChatScreen(
    state: ChatUiState,
    onBackClick: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTint)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatHeader(state = state, onBackClick = onBackClick)

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SkyBlue)
                }
            } else if (state.errorMessage != null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    StudyCard {
                        Text("Không thể mở chat", fontWeight = FontWeight.Bold)
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.messages) { message ->
                        ChatMessageRow(
                            message = message,
                            isMine = message.senderId == state.currentUserId
                        )
                    }
                }
            }

            ChatInputBar(
                value = state.input,
                onValueChange = onInputChange,
                onSendClick = onSendClick
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ChatHeader(
    state: ChatUiState,
    onBackClick: () -> Unit
) {
    val group = state.group
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBackClick) {
                Text("<")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group?.name ?: "Nhóm học tập",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${group?.memberIds?.size ?: 0} thành viên • đang trực tuyến",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(group?.memberNames.orEmpty()) { name ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Avatar(initials = name.take(1).ifBlank { "S" }, background = Lavender)
                    Text(
                        name.ifBlank { "Bạn" },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageRow(
    message: ChatMessage,
    isMine: Boolean
) {
    MessageBubble(
        sender = if (isMine) "Bạn" else message.senderName.ifBlank { "Thành viên" },
        message = message.text,
        isMine = isMine
    )
}

@Composable
private fun ChatInputBar(
    value: String,
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
            placeholder = { Text("Nhập tin nhắn...") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(SkyBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = onSendClick) {
                Text("Gửi", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
