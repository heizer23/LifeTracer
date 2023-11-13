package com.example.lifetracer.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ListItemInstanceBinding

class InstanceAdapter(
    private val context: Context,
    private var instanceList: List<InstanceWithTask>
) : RecyclerView.Adapter<InstanceAdapter.ViewHolder>() {

    var onItemClickListener: ((InstanceWithTask) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemInstanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return instanceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instance = instanceList[position]
        holder.bind(instance)

        // Set a click listener for the item view
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(instance)
        }
    }

    inner class ViewHolder(private val binding: ListItemInstanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(instance: InstanceWithTask) {
          // todo   binding.instance = instance
            binding.executePendingBindings()
        }
    }

    fun updateList(newList: List<InstanceWithTask>) {
        instanceList = newList
        notifyDataSetChanged()
    }
}
