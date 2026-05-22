package com.example.studyflow.viewmodel

enum class FocusTimerStatus {
    Idle,
    Running,
    Paused,
    Completed,
    Interrupted
}

data class FocusUiState(
    val totalSeconds: Int = DEFAULT_FOCUS_SECONDS,
    val remainingSeconds: Int = DEFAULT_FOCUS_SECONDS,
    val status: FocusTimerStatus = FocusTimerStatus.Idle,
    val startedAt: Long? = null,
    val message: String? = null
) {
    val elapsedSeconds: Int
        get() = totalSeconds - remainingSeconds

    val progress: Float
        get() = if (totalSeconds == 0) 0f else elapsedSeconds.toFloat() / totalSeconds.toFloat()

    val timerText: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }

    val subtitle: String
        get() = when (status) {
            FocusTimerStatus.Interrupted -> "Phiên làm việc bị gián đoạn"
            FocusTimerStatus.Completed -> "Hoàn thành phiên tập trung"
            else -> "Phiên làm việc sâu"
        }
}

const val DEFAULT_FOCUS_SECONDS = 25 * 60
