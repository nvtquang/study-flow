package com.example.studyflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.studyflow.R
import com.example.studyflow.data.model.UserProfile
import com.example.studyflow.databinding.DialogEditProfileBinding
import com.example.studyflow.databinding.FragmentProfileBinding
import com.example.studyflow.viewmodel.AuthViewModel
import com.example.studyflow.viewmodel.ProfileUiState
import com.example.studyflow.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private var currentProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActions()
        collectProfileState()
    }

    private fun bindActions() {
        binding.editButton.setOnClickListener { showEditDialog() }
        binding.notificationSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationSettingsFragment)
        }
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun collectProfileState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: ProfileUiState) {
        currentProfile = state.profile
        binding.loadingProgressBar.isVisible = state.isLoading
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.messageTextView.isVisible = state.message != null
        binding.messageTextView.text = state.message.orEmpty()
        binding.editButton.isEnabled = state.profile != null && !state.isSaving

        val profile = state.profile
        val displayName = profile?.displayName.orEmpty().ifBlank { "StudyFlow User" }
        binding.avatarTextView.text = displayName.initials()
        binding.nameTextView.text = displayName
        binding.emailTextView.text = profile?.email.orEmpty()
        binding.streakTextView.text = (profile?.currentStreak ?: 0).toString()
        binding.scoreTextView.text = (profile?.focusScore ?: 0).toString()
        binding.focusTimeTextView.text = "${getString(R.string.profile_focus_time)}: ${(profile?.totalFocusSeconds ?: 0L).toHoursText()}"
    }

    private fun showEditDialog() {
        val profile = currentProfile ?: return
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        dialogBinding.nameEditText.setText(profile.displayName)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.profile_edit)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.profile_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.nameEditText.text?.toString().orEmpty()
                if (name.trim().isBlank()) {
                    dialogBinding.nameLayout.error = "Tên không được để trống."
                    return@setOnClickListener
                }
                profileViewModel.updateDisplayName(name)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.logout_confirm_action) { _, _ ->
                authViewModel.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
            .show()
    }

    private fun String.initials(): String {
        val parts = trim().split(" ").filter { it.isNotBlank() }.take(2)
        if (parts.isEmpty()) return getString(R.string.studyflow_logo)
        return parts.joinToString("") { it.first().uppercase() }
    }

    private fun Long.toHoursText(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        return "${hours}h ${minutes}m"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
