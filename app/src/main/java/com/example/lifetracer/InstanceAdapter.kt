package com.example.lifetracer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InstanceAdapter(private val context: Context, private var instanceList: List<Instance>) : RecyclerView.Adapter<InstanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_instance, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return instanceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instance = instanceList[position]
        holder.bind(instance)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val instanceNameTextView: TextView = itemView.findViewById(R.id.textViewInstanceName)
        private val instanceDateTextView: TextView = itemView.findViewById(R.id.textViewInstanceDate)

        fun bind(instance: Instance) {
            instanceNameTextView.text = instance.taskName
            instanceDateTextView.text = instance.date
        }
    }

    fun updateList(newList: List<Instance>) {
        instanceList = newList
        notifyDataSetChanged()
    }

}
