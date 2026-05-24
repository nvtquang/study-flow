package com.example.studyflow.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyflow.databinding.FragmentAiBinding
import com.example.studyflow.viewmodel.AiAssistantUiState
import com.example.studyflow.viewmodel.AiAssistantViewModel
import kotlinx.coroutines.launch

class AiFragment : Fragment() {
    private var _binding: FragmentAiBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val aiViewModel: AiAssistantViewModel by viewModels()
    private val messageAdapter = AiMessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        bindActions()
        collectAiState()
    }

    private fun setupList() {
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun bindActions() {
        binding.promptEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aiViewModel.updateInput(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        binding.sendButton.setOnClickListener {
            aiViewModel.sendMessage()
        }
    }

    private fun collectAiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                aiViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: AiAssistantUiState) {
        binding.typingBar.isVisible = state.isTyping
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.emptyTextView.isVisible = state.messages.isEmpty()
        binding.sendButton.isEnabled = state.input.trim().isNotBlank() && !state.isTyping

        if (binding.promptEditText.text?.toString() != state.input) {
            binding.promptEditText.setText(state.input)
            binding.promptEditText.setSelection(binding.promptEditText.text?.length ?: 0)
        }

        val oldCount = messageAdapter.itemCount
        messageAdapter.submitList(state.messages)
        if (state.messages.isNotEmpty() && state.messages.size >= oldCount) {
            binding.messagesRecyclerView.scrollToPosition(state.messages.lastIndex)
        }
    }

    override fun onDestroyView() {
        binding.messagesRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
