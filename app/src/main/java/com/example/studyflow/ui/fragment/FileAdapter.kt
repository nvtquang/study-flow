package com.example.studyflow.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.data.model.StudyFile
import com.example.studyflow.databinding.ItemFileBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileAdapter(
    private val onFileClick: (StudyFile) -> Unit
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {
    private val files = mutableListOf<StudyFile>()

    fun submitList(nextFiles: List<StudyFile>) {
        files.clear()
        files.addAll(nextFiles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onFileClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size

    class ViewHolder(
        private val binding: ItemFileBinding,
        private val onFileClick: (StudyFile) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: StudyFile) {
            binding.nameTextView.text = file.name
            binding.typeTextView.text = file.contentType.ifBlank { file.name.extensionLabel() }
            binding.metaTextView.text = "${file.sizeBytes.formatBytes()} - ${file.createdAt.formatDate()}"
            binding.root.setOnClickListener { onFileClick(file) }
        }

        private fun String.extensionLabel(): String {
            return substringAfterLast('.', missingDelimiterValue = "File").uppercase()
        }

        private fun Long.formatDate(): String {
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))
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
    }
}
