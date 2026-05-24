package com.example.studyflow.ui.fragment

import android.app.DatePickerDialog
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
import androidx.navigation.fragment.findNavController
import com.example.studyflow.R
import com.example.studyflow.databinding.FragmentAddScheduleBinding
import com.example.studyflow.viewmodel.AddEntryType
import com.example.studyflow.viewmodel.AddScheduleUiState
import com.example.studyflow.viewmodel.AddScheduleViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class AddScheduleFragment : Fragment() {
    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val addScheduleViewModel: AddScheduleViewModel by viewModels()
    private var rendering = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActions()
        collectAddScheduleState()
    }

    private fun bindActions() {
        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (rendering) return@setOnCheckedChangeListener
            val type = if (checkedId == R.id.deadlineRadioButton) {
                AddEntryType.Deadline
            } else {
                AddEntryType.Schedule
            }
            addScheduleViewModel.selectType(type)
        }

        binding.dateEditText.setOnClickListener { showDatePicker() }
        binding.startTimeEditText.setOnClickListener { showTimePicker(isStart = true) }
        binding.endTimeEditText.setOnClickListener { showTimePicker(isStart = false) }
        binding.saveButton.setOnClickListener {
            syncFormToViewModel()
            addScheduleViewModel.save()
        }
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun collectAddScheduleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                addScheduleViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: AddScheduleUiState) {
        rendering = true
        binding.scheduleRadioButton.isChecked = state.entryType == AddEntryType.Schedule
        binding.deadlineRadioButton.isChecked = state.entryType == AddEntryType.Deadline
        binding.dateEditText.setText(state.date)
        binding.startTimeEditText.setText(state.startTime)
        binding.endTimeEditText.setText(state.endTime)
        rendering = false

        binding.saveButton.isEnabled = !state.isSaving
        binding.cancelButton.isEnabled = !state.isSaving
        binding.saveButton.text = getString(
            if (state.isSaving) R.string.loading_save else R.string.action_save
        )

        binding.messageTextView.isVisible = state.message != null
        binding.messageTextView.text = state.message.orEmpty()

        if (state.saveSuccess) {
            addScheduleViewModel.clearSaveSuccess()
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set(KEY_REFRESH, true)
            findNavController().popBackStack()
        }
    }

    private fun syncFormToViewModel() {
        addScheduleViewModel.updateTitle(binding.titleEditText.text?.toString().orEmpty())
        addScheduleViewModel.updateDate(binding.dateEditText.text?.toString().orEmpty())
        addScheduleViewModel.updateStartTime(binding.startTimeEditText.text?.toString().orEmpty())
        addScheduleViewModel.updateEndTime(binding.endTimeEditText.text?.toString().orEmpty())
        addScheduleViewModel.updateLocation(binding.locationEditText.text?.toString().orEmpty())
        addScheduleViewModel.updateNote(binding.noteEditText.text?.toString().orEmpty())
    }

    private fun showDatePicker() {
        val date = runCatching {
            LocalDate.parse(binding.dateEditText.text?.toString().orEmpty())
        }.getOrDefault(LocalDate.now())

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selected = LocalDate.of(year, month + 1, day)
                addScheduleViewModel.updateDate(selected.format(DateTimeFormatter.ISO_LOCAL_DATE))
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        ).show()
    }

    private fun showTimePicker(isStart: Boolean) {
        val currentText = if (isStart) {
            binding.startTimeEditText.text?.toString().orEmpty()
        } else {
            binding.endTimeEditText.text?.toString().orEmpty()
        }
        val time = runCatching {
            LocalTime.parse(currentText, TIME_FORMATTER)
        }.getOrDefault(LocalTime.now().withSecond(0).withNano(0))

        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val selected = LocalTime.of(hour, minute).format(TIME_FORMATTER)
                if (isStart) {
                    addScheduleViewModel.updateStartTime(selected)
                } else {
                    addScheduleViewModel.updateEndTime(selected)
                }
            },
            time.hour,
            time.minute,
            true
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val KEY_REFRESH = "planner_refresh"
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
