package com.example.studyflow.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyflow.R
import com.example.studyflow.data.model.StudyGroup
import com.example.studyflow.databinding.DialogCreateGroupBinding
import com.example.studyflow.databinding.FragmentGroupsBinding
import com.example.studyflow.viewmodel.GroupsUiState
import com.example.studyflow.viewmodel.GroupsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val groupsViewModel: GroupsViewModel by viewModels()
    private val groupAdapter = GroupAdapter(
        onGroupClick = ::openChat,
        onJoinClick = { group -> groupsViewModel.joinGroup(group.id) },
        onLeaveClick = ::confirmLeaveGroup
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        bindActions()
        collectGroupsState()
    }

    private fun setupList() {
        binding.groupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }
    }

    private fun bindActions() {
        binding.createGroupFab.setOnClickListener { showCreateGroupDialog() }
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                groupsViewModel.updateQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun collectGroupsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                groupsViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: GroupsUiState) {
        binding.loadingProgressBar.isVisible = state.isLoading
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.messageTextView.isVisible = state.message != null
        binding.messageTextView.text = state.message.orEmpty()
        binding.createGroupFab.isEnabled = !state.isCreating

        val groups = state.filteredGroups
        binding.emptyTextView.isVisible = !state.isLoading && state.errorMessage == null && groups.isEmpty()
        groupAdapter.submitList(groups, state.currentUserId, state.activeGroupActionId)
    }

    private fun openChat(group: StudyGroup) {
        findNavController().navigate(
            R.id.action_groupsFragment_to_chatFragment,
            bundleOf(ARG_GROUP_ID to group.id)
        )
    }

    private fun confirmLeaveGroup(group: StudyGroup) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Roi nhom")
            .setMessage("Ban co muon roi khoi nhom ${group.name}?")
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton("Roi nhom") { _, _ ->
                groupsViewModel.leaveGroup(group.id)
            }
            .show()
    }

    private fun showCreateGroupDialog() {
        val dialogBinding = DialogCreateGroupBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.create_group_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_create, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.nameEditText.text?.toString().orEmpty()
                if (name.trim().isBlank()) {
                    dialogBinding.nameLayout.error = "Vui lòng nhập tên nhóm."
                    return@setOnClickListener
                }
                groupsViewModel.createGroup(
                    name = name,
                    description = dialogBinding.descriptionEditText.text?.toString().orEmpty()
                )
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        binding.groupsRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val ARG_GROUP_ID = "groupId"
    }
}
