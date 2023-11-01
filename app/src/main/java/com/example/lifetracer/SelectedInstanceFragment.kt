package com.example.lifetracer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class SelectedInstanceFragment : Fragment() {
    private lateinit var textViewInstanceName: TextView
    private lateinit var textViewInstanceDate: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonPause: Button
    private lateinit var instance: Instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_selected_instance, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewInstanceName = view.findViewById(R.id.textViewInstanceName)
        textViewInstanceDate = view.findViewById(R.id.textViewInstanceDate)
        buttonStart = view.findViewById(R.id.buttonStart)
        buttonPause = view.findViewById(R.id.buttonPause)

        // Update the text views with instance data
        if (::instance.isInitialized) {
            // Update the text views with instance data
            textViewInstanceName.text = "huhu" //instance.taskName
            textViewInstanceDate.text = instance.date
        }

        // Set click listeners for buttons (handle actions in the activity)
        buttonStart.setOnClickListener {
            // Communicate the "Start" action to the parent activity
            (activity as? SelectedInstanceListener)?.onStartButtonClicked(instance)
        }

        buttonPause.setOnClickListener {
            // Communicate the "Pause" action to the parent activity
            (activity as? SelectedInstanceListener)?.onPauseButtonClicked(instance)
        }
    }

    // Update the selected instance details
    fun updateSelectedView(instance: Instance) {
        textViewInstanceName.text = instance.taskName
        textViewInstanceDate.text = instance.date
        // Update other views as needed
    }


    // Interface for communication with the parent activity
    interface SelectedInstanceListener {
        fun onStartButtonClicked(instance: Instance)
        fun onPauseButtonClicked(instance: Instance)
    }

    // Set the selected instance to display
    fun setInstance(instance: Instance) {
        this.instance = instance
    }
}
