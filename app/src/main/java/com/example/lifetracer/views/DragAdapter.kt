package com.example.lifetracer.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemVaultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections

class DragAdapter(
    private val scope: CoroutineScope,
    private val onDragEnd: (List<InstanceWithTask>) -> Unit
) : RecyclerView.Adapter<DragAdapter.ViewHolder>() {

    private val mutableCurrentList = mutableListOf<InstanceWithTask>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemVaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instanceWithTask = mutableCurrentList[position]
        holder.bind(instanceWithTask)
    }

    override fun getItemCount(): Int = mutableCurrentList.size

    fun setData(newData: List<InstanceWithTask>) {
        mutableCurrentList.clear()
        mutableCurrentList.addAll(newData)
        notifyDataSetChanged()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mutableCurrentList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mutableCurrentList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onDragEnded() {
        // Update priorities in the mutable list
        for (i in mutableCurrentList.indices) {
            mutableCurrentList[i] = mutableCurrentList[i].copy(priority = i)
        }

        // Notify via the onDragEnd callback
        scope.launch(Dispatchers.IO) {
            onDragEnd(mutableCurrentList)
        }
    }

    class ViewHolder(
        private val binding: ListItemVaultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(instanceWithTask: InstanceWithTask) {
            binding.instanceWithTask = instanceWithTask
            binding.executePendingBindings()
        }
    }
}
