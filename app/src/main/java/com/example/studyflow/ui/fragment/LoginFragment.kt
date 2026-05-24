package com.example.studyflow.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.studyflow.R
import com.example.studyflow.databinding.FragmentLoginBinding
import com.example.studyflow.viewmodel.AuthUiState
import com.example.studyflow.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActions()
        collectAuthState()
    }

    private fun bindActions() {
        binding.loginButton.setOnClickListener {
            authViewModel.signIn(
                binding.emailEditText.text?.toString().orEmpty(),
                binding.passwordEditText.text?.toString().orEmpty()
            )
        }

        binding.registerLinkButton.setOnClickListener {
            authViewModel.clearError()
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun collectAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    render(state)
                    if (state.isAuthenticated) {
                        navigateToHome()
                    }
                }
            }
        }
    }

    private fun render(state: AuthUiState) {
        binding.loadingProgressBar.isVisible = state.isLoading
        binding.loginButton.isEnabled = !state.isLoading
        binding.registerLinkButton.isEnabled = !state.isLoading
        binding.loginButton.text = getString(
            if (state.isLoading) R.string.loading_login else R.string.action_login
        )

        val errorMessage = state.errorMessage
        binding.errorTextView.isVisible = errorMessage != null
        binding.errorTextView.text = errorMessage.orEmpty()
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
    }

    private fun navigateToHome() {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.loginFragment) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
