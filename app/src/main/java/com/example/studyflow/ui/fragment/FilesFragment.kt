package com.example.studyflow.ui.fragment

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyflow.R
import com.example.studyflow.data.model.StudyFile
import com.example.studyflow.databinding.FragmentFilesBinding
import com.example.studyflow.viewmodel.FileSortOption
import com.example.studyflow.viewmodel.FilesUiState
import com.example.studyflow.viewmodel.FilesViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class FilesFragment : Fragment() {
    private var _binding: FragmentFilesBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val filesViewModel: FilesViewModel by viewModels()
    private val fileAdapter = FileAdapter(::openFile)
    private val filePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val meta = uri.readFileMeta()
            filesViewModel.uploadFile(
                uri = uri,
                fileName = meta.name,
                contentType = meta.contentType
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        bindActions()
        collectFilesState()
    }

    private fun setupList() {
        binding.filesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fileAdapter
        }
    }

    private fun bindActions() {
        binding.uploadFab.setOnClickListener {
            filePicker.launch(arrayOf("*/*"))
        }
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filesViewModel.updateQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        binding.sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val option = when (checkedId) {
                R.id.nameRadioButton -> FileSortOption.Name
                R.id.sizeRadioButton -> FileSortOption.Size
                else -> FileSortOption.Recent
            }
            filesViewModel.selectSort(option)
        }
    }

    private fun collectFilesState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filesViewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: FilesUiState) {
        binding.loadingProgressBar.isVisible = state.isLoading || state.isUploading
        binding.uploadFab.isEnabled = !state.isUploading
        binding.errorTextView.isVisible = state.errorMessage != null
        binding.errorTextView.text = state.errorMessage.orEmpty()
        binding.messageTextView.isVisible = state.message != null
        binding.messageTextView.text = state.message.orEmpty()
        binding.emptyTextView.isVisible = !state.isLoading && state.errorMessage == null && state.filteredFiles.isEmpty()
        binding.storageTextView.text = "${getString(R.string.files_total_storage)}: ${state.totalBytes.formatBytes()}"

        when (state.sortOption) {
            FileSortOption.Recent -> binding.recentRadioButton.isChecked = true
            FileSortOption.Name -> binding.nameRadioButton.isChecked = true
            FileSortOption.Size -> binding.sizeRadioButton.isChecked = true
        }

        fileAdapter.submitList(state.filteredFiles)
    }

    private fun openFile(file: StudyFile) {
        if (file.downloadUrl.isBlank()) return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(file.downloadUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { startActivity(intent) }
    }

    private fun Uri.readFileMeta(): PickedFileMeta {
        var name = lastPathSegment.orEmpty().substringAfterLast('/')
        val contentType = requireContext().contentResolver.getType(this).orEmpty()
        val cursor: Cursor? = requireContext().contentResolver.query(this, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex).orEmpty().ifBlank { name }
            }
        }
        return PickedFileMeta(
            name = name.ifBlank { "studyflow-file" },
            contentType = contentType
        )
    }

    private fun Long.formatBytes(): String {
        if (this <= 0L) return "0 B"
        val units = listOf("B", "KB", "MB", "GB")
        var value = this.toDouble()
        var unitIndex = 0
        while (value >= 1024 && unitIndex < units.lastIndex) {
            value /= 1024
            unitIndex++
        }
        return if (unitIndex == 0) {
            "${value.toLong()} ${units[unitIndex]}"
        } else {
            String.format(Locale.getDefault(), "%.1f %s", value, units[unitIndex])
        }
    }

    override fun onDestroyView() {
        binding.filesRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private data class PickedFileMeta(
        val name: String,
        val contentType: String
    )
}
