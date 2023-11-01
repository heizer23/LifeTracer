package com.example.lifetracer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.databinding.ListItemInstanceBinding

class InstanceAdapter(
    private val context: Context,
    private var instanceList: List<Instance>
) : RecyclerView.Adapter<InstanceAdapter.ViewHolder>() {

    var onItemClickListener: ((Instance) -> Unit)? = null

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
        fun bind(instance: Instance) {
            binding.instance = instance
            binding.executePendingBindings()
        }
    }

    fun updateList(newList: List<Instance>) {
        instanceList = newList
        notifyDataSetChanged()
    }
}
