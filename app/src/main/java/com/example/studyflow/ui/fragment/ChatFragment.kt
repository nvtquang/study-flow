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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyflow.databinding.FragmentChatBinding
import com.example.studyflow.viewmodel.ChatUiState
import com.example.studyflow.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val chatViewModel: ChatViewModel by viewModels()
    private val messageAdapter = ChatMessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        bindActions()
        collectChatState()
        chatViewModel.start(requireArguments().getString(ARG_GROUP_ID).orEmpty())
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
        binding.backTextView.setOnClickListener { findNavController().popBackStack() }
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                chatViewModel.updateInput(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        binding.sendButton.setOnClickListener {
            chatViewModel.sendMessage()
        }
    }

    private fun collectChatState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: ChatUiState) {
        binding.loadingProgressBar.isVisible = state.isLoading
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.emptyTextView.isVisible = !state.isLoading && state.errorMessage == null && state.messages.isEmpty()
        binding.groupNameTextView.text = state.group?.name.orEmpty().ifBlank { "Nhóm học tập" }
        binding.groupMetaTextView.text = "${state.group?.memberIds?.size ?: 0} thành viên"

        if (binding.messageEditText.text?.toString() != state.input) {
            binding.messageEditText.setText(state.input)
            binding.messageEditText.setSelection(binding.messageEditText.text?.length ?: 0)
        }
        binding.sendButton.isEnabled = state.input.trim().isNotBlank() && !state.isLoading

        val oldCount = messageAdapter.itemCount
        messageAdapter.submitList(state.messages, state.currentUserId)
        if (state.messages.isNotEmpty() && state.messages.size >= oldCount) {
            binding.messagesRecyclerView.scrollToPosition(state.messages.lastIndex)
        }
    }

    override fun onDestroyView() {
        binding.messagesRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val ARG_GROUP_ID = "groupId"
    }
}
