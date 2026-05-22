package com.example.studyflow.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.SecondaryButton
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.FocusTimerStatus
import com.example.studyflow.viewmodel.FocusUiState

@Composable
fun FocusScreen(
    state: FocusUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onRetry: () -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            FocusHeader(state = state)
            TimerCard(state = state)

            when (state.status) {
                FocusTimerStatus.Idle,
                FocusTimerStatus.Completed,
                FocusTimerStatus.Interrupted -> {
                    PrimaryButton(text = "Bắt đầu Phiên", onClick = onStart)
                }
                FocusTimerStatus.Running -> {
                    PrimaryButton(text = "Tạm dừng", onClick = onPause)
                }
                FocusTimerStatus.Paused -> {
                    PrimaryButton(text = "Tiếp tục", onClick = onResume)
                }
            }

            SecondaryButton(text = "ĐẶT LẠI", onClick = onReset)

            if (state.status == FocusTimerStatus.Interrupted) {
                WiltedTreeCard(onRetry = onRetry)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FocusHeader(state: FocusUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "StudyFlow",
            style = MaterialTheme.typography.titleMedium,
            color = SkyBlue,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Chế độ Tập trung",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = state.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimerCard(state: FocusUiState) {
    StudyCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            FocusProgressRing(progress = state.progress)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.timerText,
                    style = MaterialTheme.typography.displayMedium,
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (state.status) {
                        FocusTimerStatus.Running -> "Đang tập trung"
                        FocusTimerStatus.Paused -> "Đang tạm dừng"
                        FocusTimerStatus.Completed -> "Hoàn thành"
                        FocusTimerStatus.Interrupted -> "Bị gián đoạn"
                        FocusTimerStatus.Idle -> "Sẵn sàng"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "Một phiên Pomodoro mặc định kéo dài 25 phút. Giữ màn hình này mở để theo dõi phiên tập trung.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FocusProgressRing(progress: Float) {
    Canvas(modifier = Modifier.size(240.dp)) {
        val strokeWidth = 16.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = StudyBorder.copy(alpha = 0.6f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            brush = Brush.linearGradient(listOf(SkyBlue, Lavender)),
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun WiltedTreeCard(onRetry: () -> Unit) {
    StudyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(Lavender, SkyBlue)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("!", color = CardWhite, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cây của bạn đã héo mất rồi!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Phiên tập trung bị đặt lại giữa chừng.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        TextButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text("Thử lại", fontWeight = FontWeight.Bold)
        }
    }
}
