package com.example.studyflow.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.data.model.StudyTask
import com.example.studyflow.databinding.ItemPlannerEntryBinding

class PlannerEntryAdapter : RecyclerView.Adapter<PlannerEntryAdapter.ViewHolder>() {
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
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size

    class ViewHolder(
        private val binding: ItemPlannerEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: PlannerEntry) {
            binding.startTimeTextView.text = entry.startTime
            binding.endTimeTextView.text = entry.endTime
            binding.titleTextView.text = entry.title
            binding.metaTextView.text = "${entry.type} - ${entry.location}"
            binding.noteTextView.text = entry.note
            binding.noteTextView.isVisible = entry.note.isNotBlank()
        }
    }
}

data class PlannerEntry(
    val id: String,
    val title: String,
    val type: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val note: String
)

fun StudySchedule.toPlannerEntry(): PlannerEntry {
    return PlannerEntry(
        id = id,
        title = title,
        type = eventType,
        date = date,
        startTime = startTime,
        endTime = endTime,
        location = location,
        note = note
    )
}

fun StudyTask.toPlannerEntry(): PlannerEntry {
    return PlannerEntry(
        id = id,
        title = title,
        type = eventType,
        date = date,
        startTime = startTime,
        endTime = endTime,
        location = location,
        note = note
    )
}
