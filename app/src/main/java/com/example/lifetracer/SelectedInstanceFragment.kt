package com.example.lifetracer

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.fragment.app.Fragment

class SelectedInstanceFragment : Fragment() {
    private lateinit var textViewInstanceName: TextView
    private lateinit var textViewInstanceDate: TextView
    private lateinit var textViewDuration: TextView


    private lateinit var buttonStart: Button
    private lateinit var buttonPause: Button


    private lateinit var chronometer: Chronometer
    private var timerRunning = false
    private var elapsedTime: Long = 0

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
        textViewDuration = view.findViewById(R.id.textViewDuration)
        chronometer = view.findViewById(R.id.chronometer)

        buttonStart = view.findViewById(R.id.buttonStart)
        buttonPause = view.findViewById(R.id.buttonPause)

        chronometer.setOnChronometerTickListener {
            if (timerRunning) {
                elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
                val h = (elapsedTime / 3600000).toInt()
                val m = (elapsedTime - h * 3600000).toInt() / 60000
                val s = (elapsedTime - h * 3600000 - m * 60000).toInt() / 1000
                textViewDuration.text = String.format("%02d:%02d:%02d", h, m, s)
            }
        }

        // Update the text views with instance data
        if (::instance.isInitialized) {
            // Update the text views with instance data
            textViewInstanceName.text = "huhu" //instance.taskName
            textViewInstanceDate.text = instance.date
        }

        buttonStart.setOnClickListener {
            if (!timerRunning) {
                // Start the timer
                chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
                chronometer.start()
                timerRunning = true

                // Update the instance status to 1 (assuming you have a reference to the instance)
          //      instance.status = 1

                // Call a method in the Controller to update the instance's status
            //    Controller.updateInstanceStatus(instance.id, instance.status)
            }
        }

        buttonPause.setOnClickListener {
            if (timerRunning) {
                // Pause the timer
                chronometer.stop()
                elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
                timerRunning = false
            }
        }
    }

    // Update the selected instance details
    fun updateSelectedView(instance: Instance) {
        this.instance = instance
        textViewInstanceName.text = instance.taskName
        textViewInstanceDate.text = instance.date
        // Update other views as needed
    }


}
