package com.example.studyflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studyflow.R
import com.example.studyflow.databinding.FragmentFocusBinding
import com.example.studyflow.viewmodel.FocusTimerStatus
import com.example.studyflow.viewmodel.FocusUiState
import com.example.studyflow.viewmodel.FocusViewModel
import kotlinx.coroutines.launch

class FocusFragment : Fragment() {
    private var _binding: FragmentFocusBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val focusViewModel: FocusViewModel by viewModels()
    private var currentStatus: FocusTimerStatus = FocusTimerStatus.Idle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFocusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActions()
        collectFocusState()
    }

    private fun bindActions() {
        binding.primaryActionButton.setOnClickListener {
            when (currentStatus) {
                FocusTimerStatus.Running -> focusViewModel.pause()
                FocusTimerStatus.Paused -> focusViewModel.resume()
                FocusTimerStatus.Idle,
                FocusTimerStatus.Completed,
                FocusTimerStatus.Interrupted -> focusViewModel.start()
            }
        }
        binding.resetButton.setOnClickListener {
            focusViewModel.reset()
        }
        binding.retryButton.setOnClickListener {
            focusViewModel.retry()
        }
    }

    private fun collectFocusState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                focusViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: FocusUiState) {
        currentStatus = state.status
        binding.subtitleTextView.text = state.subtitle
        binding.timerTextView.text = state.timerText
        binding.statusTextView.text = statusText(state.status)
        binding.progressIndicator.progress = (state.progress * 100).toInt().coerceIn(0, 100)
        binding.elapsedTextView.text = state.elapsedSeconds.formatDuration()
        binding.remainingTextView.text = state.remainingSeconds.formatDuration()
        binding.primaryActionButton.text = primaryActionText(state.status)
        binding.messageTextView.isVisible = state.message != null
        binding.messageTextView.text = state.message.orEmpty()
        binding.interruptedCard.isVisible = state.status == FocusTimerStatus.Interrupted
        binding.resetButton.isEnabled = state.status != FocusTimerStatus.Idle || state.elapsedSeconds > 0
    }

    private fun statusText(status: FocusTimerStatus): String {
        val resId = when (status) {
            FocusTimerStatus.Idle -> R.string.focus_status_ready
            FocusTimerStatus.Running -> R.string.focus_status_running
            FocusTimerStatus.Paused -> R.string.focus_status_paused
            FocusTimerStatus.Completed -> R.string.focus_status_completed
            FocusTimerStatus.Interrupted -> R.string.focus_status_interrupted
        }
        return getString(resId)
    }

    private fun primaryActionText(status: FocusTimerStatus): String {
        val resId = when (status) {
            FocusTimerStatus.Running -> R.string.focus_action_pause
            FocusTimerStatus.Paused -> R.string.focus_action_resume
            FocusTimerStatus.Idle,
            FocusTimerStatus.Completed,
            FocusTimerStatus.Interrupted -> R.string.focus_action_start
        }
        return getString(resId)
    }

    private fun Int.formatDuration(): String {
        val minutes = this / 60
        val seconds = this % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
