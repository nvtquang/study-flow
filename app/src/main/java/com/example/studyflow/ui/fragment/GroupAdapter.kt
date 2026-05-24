package com.example.studyflow.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.data.model.StudyGroup
import com.example.studyflow.databinding.ItemGroupBinding

class GroupAdapter(
    private val onGroupClick: (StudyGroup) -> Unit
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    private val groups = mutableListOf<StudyGroup>()

    fun submitList(nextGroups: List<StudyGroup>) {
        groups.clear()
        groups.addAll(nextGroups)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onGroupClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    class ViewHolder(
        private val binding: ItemGroupBinding,
        private val onGroupClick: (StudyGroup) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: StudyGroup) {
            binding.iconTextView.text = group.subjectIcon.ifBlank { group.name.take(1).uppercase() }
            binding.nameTextView.text = group.name
            binding.descriptionTextView.text = group.description.ifBlank { "Nhóm học tập StudyFlow" }
            binding.metaTextView.text = "${group.memberIds.size} thành viên"
            binding.root.setOnClickListener { onGroupClick(group) }
        }
    }
}
