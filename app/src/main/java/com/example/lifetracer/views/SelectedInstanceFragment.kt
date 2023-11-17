package com.example.lifetracer.views

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.lifetracer.ViewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.FragmentSelectedInstanceBinding

class SelectedInstanceFragment : Fragment() {
    private lateinit var binding: FragmentSelectedInstanceBinding

    private val viewModel: InstancesViewModel by activityViewModels()

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

        viewModel.selectedInstance.observe(viewLifecycleOwner, Observer { instance ->
            instance?.let {
                updateSelectedView(it)
            }
        })

        binding.buttonStart.setOnClickListener {
            viewModel.startCurrentInstance()
            startTimer()
        }
        binding.buttonPause.setOnClickListener {
            viewModel.pauseCurrentInstance()
            pauseTimer()
        }
        binding.buttonFinish.setOnClickListener {
            viewModel.finishSelectedInstance()
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
    fun updateSelectedView(instance: InstanceWithTask) {
        binding.textViewInstanceName.text = instance.task.name
        binding.textViewInstanceDate.text = instance.instance.date
        // Update other views as needed
    }
}
