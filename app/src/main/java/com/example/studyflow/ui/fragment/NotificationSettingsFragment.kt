package com.example.studyflow.ui.fragment

import android.app.TimePickerDialog
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
import com.example.studyflow.databinding.FragmentNotificationSettingsBinding
import com.example.studyflow.viewmodel.NotificationSettingsUiState
import com.example.studyflow.viewmodel.NotificationSettingsViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class NotificationSettingsFragment : Fragment() {
    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: NotificationSettingsViewModel by viewModels()
    private var rendering = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActions()
        collectSettingsState()
    }

    private fun bindActions() {
        binding.classRemindersSwitch.setOnCheckedChangeListener { _, checked ->
            if (!rendering) viewModel.update { it.copy(classReminders = checked) }
        }
        binding.deadlineAlertsSwitch.setOnCheckedChangeListener { _, checked ->
            if (!rendering) viewModel.update { it.copy(deadlineAlerts = checked) }
        }
        binding.focusRemindersSwitch.setOnCheckedChangeListener { _, checked ->
            if (!rendering) viewModel.update { it.copy(focusReminders = checked) }
        }
        binding.groupChatSwitch.setOnCheckedChangeListener { _, checked ->
            if (!rendering) viewModel.update { it.copy(groupChatNotifications = checked) }
        }
        binding.dailySummarySwitch.setOnCheckedChangeListener { _, checked ->
            if (!rendering) viewModel.update { it.copy(dailySummary = checked) }
        }
        binding.summaryTimeEditText.setOnClickListener { showTimePicker() }
    }

    private fun collectSettingsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: NotificationSettingsUiState) {
        rendering = true
        binding.classRemindersSwitch.isChecked = state.settings.classReminders
        binding.deadlineAlertsSwitch.isChecked = state.settings.deadlineAlerts
        binding.focusRemindersSwitch.isChecked = state.settings.focusReminders
        binding.groupChatSwitch.isChecked = state.settings.groupChatNotifications
        binding.dailySummarySwitch.isChecked = state.settings.dailySummary
        binding.summaryTimeEditText.setText(state.settings.summaryTime)
        rendering = false

        binding.loadingProgressBar.isVisible = state.isLoading || state.isSaving
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.messageTextView.isVisible = state.message != null
        binding.messageTextView.text = state.message.orEmpty()
    }

    private fun showTimePicker() {
        val current = runCatching {
            LocalTime.parse(binding.summaryTimeEditText.text?.toString().orEmpty(), TIME_FORMATTER)
        }.getOrDefault(LocalTime.of(20, 0))

        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val selected = LocalTime.of(hour, minute).format(TIME_FORMATTER)
                viewModel.update { it.copy(summaryTime = selected) }
            },
            current.hour,
            current.minute,
            true
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
