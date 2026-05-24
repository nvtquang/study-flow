package com.example.studyflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyflow.R
import com.example.studyflow.databinding.FragmentPlannerBinding
import com.example.studyflow.viewmodel.PlannerUiState
import com.example.studyflow.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch

class PlannerFragment : Fragment() {
    private var _binding: FragmentPlannerBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val plannerViewModel: PlannerViewModel by viewModels()
    private val plannerEntryAdapter = PlannerEntryAdapter()
    private var datesBound = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        bindActions()
        observeRefreshResult()
        collectPlannerState()
    }

    private fun setupRecyclerView() {
        binding.plannerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plannerEntryAdapter
        }
    }

    private fun bindActions() {
        binding.addScheduleFab.setOnClickListener {
            findNavController().navigate(R.id.action_plannerFragment_to_addScheduleFragment)
        }
    }

    private fun observeRefreshResult() {
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>(KEY_REFRESH)
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                if (shouldRefresh == true) {
                    plannerViewModel.refresh()
                    findNavController().currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(KEY_REFRESH, false)
                }
            }
    }

    private fun collectPlannerState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                plannerViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: PlannerUiState) {
        if (!datesBound) {
            bindDateOptions(state)
            datesBound = true
        }

        binding.loadingProgressBar.isVisible = state.isLoading
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.emptyTextView.isVisible = state.isEmpty && state.errorMessage == null

        val entries = buildList {
            addAll(state.schedules.map { it.toPlannerEntry() })
            addAll(state.tasks.map { it.toPlannerEntry() })
        }.sortedBy { it.startTime }
        plannerEntryAdapter.submitList(entries)
    }

    private fun bindDateOptions(state: PlannerUiState) {
        binding.dateRadioGroup.removeAllViews()
        state.dateOptions.forEachIndexed { index, date ->
            val button = RadioButton(requireContext()).apply {
                id = View.generateViewId()
                text = date
                tag = date
                isChecked = date == state.selectedDate
                setPadding(0, 0, 18, 0)
            }
            binding.dateRadioGroup.addView(button)
            if (index == 0 && state.selectedDate.isBlank()) {
                button.isChecked = true
            }
        }

        binding.dateRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val date = group.findViewById<RadioButton>(checkedId)?.tag as? String
            if (!date.isNullOrBlank()) {
                plannerViewModel.selectDate(date)
            }
        }
    }

    override fun onDestroyView() {
        binding.plannerRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val KEY_REFRESH = "planner_refresh"
    }
}
