package com.example.studyflow.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.data.model.StudyGroup
import com.example.studyflow.databinding.ItemGroupBinding

class GroupAdapter(
    private val onGroupClick: (StudyGroup) -> Unit,
    private val onJoinClick: (StudyGroup) -> Unit,
    private val onLeaveClick: (StudyGroup) -> Unit
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    private val groups = mutableListOf<StudyGroup>()
    private var currentUserId: String = ""
    private var activeGroupActionId: String? = null

    fun submitList(nextGroups: List<StudyGroup>, userId: String, actionGroupId: String?) {
        groups.clear()
        groups.addAll(nextGroups)
        currentUserId = userId
        activeGroupActionId = actionGroupId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onGroupClick, onJoinClick, onLeaveClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position], currentUserId, activeGroupActionId)
    }

    override fun getItemCount(): Int = groups.size

    class ViewHolder(
        private val binding: ItemGroupBinding,
        private val onGroupClick: (StudyGroup) -> Unit,
        private val onJoinClick: (StudyGroup) -> Unit,
        private val onLeaveClick: (StudyGroup) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: StudyGroup, currentUserId: String, activeGroupActionId: String?) {
            val isMember = currentUserId in group.memberIds
            val isBusy = activeGroupActionId == group.id
            binding.iconTextView.text = group.subjectIcon.ifBlank { group.name.take(1).uppercase() }
            binding.nameTextView.text = group.name
            binding.descriptionTextView.text = group.description.ifBlank { "Nhom hoc tap StudyFlow" }
            binding.metaTextView.text = "${group.memberIds.size} thanh vien"
            binding.actionButton.text = if (isMember) "Vao chat" else "Tham gia"
            binding.actionButton.isEnabled = !isBusy
            binding.leaveButton.isVisible = isMember
            binding.leaveButton.isEnabled = !isBusy
            binding.root.setOnClickListener {
                if (isMember) onGroupClick(group) else onJoinClick(group)
            }
            binding.actionButton.setOnClickListener {
                if (isMember) onGroupClick(group) else onJoinClick(group)
            }
            binding.leaveButton.setOnClickListener { onLeaveClick(group) }
        }
    }
}
