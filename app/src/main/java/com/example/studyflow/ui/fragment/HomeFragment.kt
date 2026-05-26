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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyflow.R
import com.example.studyflow.databinding.FragmentHomeBinding
import com.example.studyflow.viewmodel.DashboardTimelineItem
import com.example.studyflow.viewmodel.HomeDashboardData
import com.example.studyflow.viewmodel.HomeUiState
import com.example.studyflow.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val homeViewModel: HomeViewModel by viewModels()
    private val timelineAdapter = DashboardItemAdapter()
    private val activityAdapter = DashboardItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLists()
        bindActions()
        collectHomeState()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadHome()
    }

    private fun setupLists() {
        binding.timelineRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timelineAdapter
            isNestedScrollingEnabled = false
        }
        binding.activityRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = activityAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun bindActions() {
        binding.retryButton.setOnClickListener { homeViewModel.loadHome() }
        binding.addScheduleButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addScheduleFragment)
        }
        binding.plannerButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_plannerFragment)
        }
        binding.focusButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_focusFragment)
        }
        binding.groupsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_groupsFragment)
        }
        binding.filesButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_filesFragment)
        }
        binding.aiButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_aiFragment)
        }
    }

    private fun collectHomeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: HomeUiState) {
        renderHeader(state)
        binding.loadingCard.isVisible = state.isLoading
        binding.errorCard.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.emptyTextView.isVisible = state.isEmpty && state.errorMessage == null
        renderDashboard(state.dashboard)
    }

    private fun renderHeader(state: HomeUiState) {
        val profile = state.profile
        val displayName = profile?.displayName.orEmpty()
        binding.greetingTextView.text = if (displayName.isBlank()) {
            getString(R.string.home_greeting_default)
        } else {
            "Xin chào, $displayName"
        }
        binding.emailTextView.text = profile?.email.orEmpty()
        binding.emailTextView.isVisible = profile?.email?.isNotBlank() == true
        binding.avatarTextView.text = displayName.initials()
    }

    private fun renderDashboard(dashboard: HomeDashboardData) {
        binding.taskCountTextView.text = dashboard.taskCount.toString()
        binding.remainingCountTextView.text = dashboard.remainingCount.toString()
        binding.nextScheduleTextView.text = "${dashboard.nextSchedule.time} - ${dashboard.nextSchedule.title}"

        val weeklyProgress = (dashboard.weeklyGoalProgress * 100).toInt().coerceIn(0, 100)
        binding.weeklyProgressBar.progress = weeklyProgress
        binding.weeklyProgressTextView.text = "$weeklyProgress%"

        timelineAdapter.submitList(
            dashboard.timeline.map { item -> item.toDashboardItem() }
        )
        activityAdapter.submitList(
            dashboard.recentActivities.map { activity ->
                DashboardItem(marker = "OK", title = activity)
            }
        )
    }

    override fun onDestroyView() {
        binding.timelineRecyclerView.adapter = null
        binding.activityRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private fun DashboardTimelineItem.toDashboardItem(): DashboardItem {
        return DashboardItem(marker = time, title = title)
    }

    private fun String.initials(): String {
        val parts = trim().split(" ").filter { it.isNotBlank() }.take(2)
        if (parts.isEmpty()) return getString(R.string.studyflow_logo)
        return parts.joinToString("") { it.first().uppercase() }
    }

}
