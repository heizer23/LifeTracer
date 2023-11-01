package com.example.lifetracer.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lifetracer.Instance
import com.example.lifetracer.databinding.FragmentSelectedInstanceBinding

class SelectedInstanceFragment : Fragment() {
    private lateinit var binding: FragmentSelectedInstanceBinding
    private var instance: Instance? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectedInstanceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access your views through the binding object

        instance?.let { nonNullInstance ->
            val instance = nonNullInstance
            binding.textViewInstanceName.text = instance.taskName
            binding.textViewInstanceDate.text = instance.date
        }



        binding.buttonStart.setOnClickListener {
            // Start button action
            // Example: Start the timer and update the instance
            startTimer()
        }

        binding.buttonPause.setOnClickListener {
            // Pause button action
            // Example: Pause the timer
            pauseTimer()
        }
    }

    private var timerRunning = false
    private var elapsedTime: Long = 0

    private fun startTimer() {
        if (!timerRunning) {
            binding.chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
            binding.chronometer.start()
            timerRunning = true
        }
    }

    private fun pauseTimer() {
        if (timerRunning) {
            binding.chronometer.stop()
            elapsedTime = SystemClock.elapsedRealtime() - binding.chronometer.base
            timerRunning = false
        }
    }

    // Update the selected instance details
    fun updateSelectedView(instance: Instance) {
        this.instance = instance
        binding.textViewInstanceName.text = instance.taskName
        binding.textViewInstanceDate.text = instance.date
        // Update other views as needed
    }
}
