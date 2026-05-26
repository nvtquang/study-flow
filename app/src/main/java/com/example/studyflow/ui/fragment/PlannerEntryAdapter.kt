package com.example.studyflow.ui.fragment

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.data.model.StudyTask
import com.example.studyflow.databinding.ItemPlannerEntryBinding

class PlannerEntryAdapter(
    private val onCompletionChanged: (PlannerEntry) -> Unit,
    private val onEdit: (PlannerEntry) -> Unit,
    private val onDelete: (PlannerEntry) -> Unit
) : RecyclerView.Adapter<PlannerEntryAdapter.ViewHolder>() {
    private val entries = mutableListOf<PlannerEntry>()

    fun submitList(nextEntries: List<PlannerEntry>) {
        entries.clear()
        entries.addAll(nextEntries)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlannerEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onCompletionChanged, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size

    class ViewHolder(
        private val binding: ItemPlannerEntryBinding,
        private val onCompletionChanged: (PlannerEntry) -> Unit,
        private val onEdit: (PlannerEntry) -> Unit,
        private val onDelete: (PlannerEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: PlannerEntry) {
            binding.startTimeTextView.text = entry.startTime
            binding.endTimeTextView.text = entry.endTime
            binding.titleTextView.text = entry.title
            binding.metaTextView.text = "${entry.type} - ${entry.location}"
            binding.noteTextView.text = entry.note
            binding.noteTextView.isVisible = entry.note.isNotBlank()
            binding.completedCheckBox.setOnCheckedChangeListener(null)
            binding.completedCheckBox.isChecked = entry.isCompleted
            binding.completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCompletionChanged(entry.copy(isCompleted = isChecked))
            }
            binding.titleTextView.paintFlags = if (entry.isCompleted) {
                binding.titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            binding.editButton.setOnClickListener { onEdit(entry) }
            binding.deleteButton.setOnClickListener { onDelete(entry) }
        }
    }
}

enum class PlannerEntryKind {
    Schedule,
    Task
}

data class PlannerEntry(
    val id: String,
    val kind: PlannerEntryKind,
    val title: String,
    val type: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val note: String,
    val isCompleted: Boolean
)

fun StudySchedule.toPlannerEntry(): PlannerEntry {
    return PlannerEntry(
        id = id,
        kind = PlannerEntryKind.Schedule,
        title = title,
        type = eventType,
        date = date,
        startTime = startTime,
        endTime = endTime,
        location = location,
        note = note,
        isCompleted = isCompleted
    )
}

fun StudyTask.toPlannerEntry(): PlannerEntry {
    return PlannerEntry(
        id = id,
        kind = PlannerEntryKind.Task,
        title = title,
        type = eventType,
        date = date,
        startTime = startTime,
        endTime = endTime,
        location = location,
        note = note,
        isCompleted = isCompleted
    )
}
