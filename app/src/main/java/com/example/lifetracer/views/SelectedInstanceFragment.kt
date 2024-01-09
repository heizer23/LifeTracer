package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.lifetracer.charts.ChartManager
import com.example.lifetracer.data.Instance
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.FragmentSelectedInstanceBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration

class SelectedInstanceFragment : Fragment() {
    private lateinit var binding: FragmentSelectedInstanceBinding
    private val viewModel: InstancesViewModel by activityViewModels()

    private lateinit var chartManager: ChartManager


    // Job is for updating duration and pause
    private var uiUpdateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedInstanceBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      //  chartManager = ChartManager(binding.barChart)
      //  chartManager.setupChart(instanceWithHistory.history)
      //  chartViewModel.chartData.observe(viewLifecycleOwner) { data ->
      //      chartManager.setupChart(data, "Instance Data")
      //  }


        viewModel.instanceWithLowestPrio.observe(viewLifecycleOwner, Observer { instanceWithTask ->
            instanceWithTask?.let {
                updateSelectedView(it)
                binding.instanceWithTask = it
            }
        })

        binding.viewModel = viewModel // Check if viewModel is not null
        binding.lifecycleOwner = viewLifecycleOwner // Important for LiveData binding

        binding.buttonFinish.setOnClickListener {
            val qualityInput = binding.editTextQuality.text.toString()
            val quantityInput = binding.editTextQuantity.text.toString()

            val instanceWithTask = viewModel.instanceWithLowestPrio.value
            if (instanceWithTask != null && viewModel.canFinishInstance(instanceWithTask, qualityInput, quantityInput)) {
                viewModel.finishActiveInstance(qualityInput, quantityInput)
            } else {
                // Show error message
                Toast.makeText(context, "Please fill in the required fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonDetails.setOnClickListener {
            val instanceWithTask = viewModel.instanceWithLowestPrio.value

            val intent = Intent(context, InstanceDetailActivity::class.java)
            if (instanceWithTask != null) {
                intent.putExtra("INSTANCE_ID_EXTRA", instanceWithTask.instance.id)
            } // Replace 'instanceId' with the actual instance ID
            startActivity(intent)
        }


    }



    private fun startUiUpdater() {
        uiUpdateJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val instanceWithTask = viewModel.instanceWithLowestPrio.value
                instanceWithTask?.let {
                    updateUi(it.instance)
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun updateUi(instance: Instance) {
        val currentTime = System.currentTimeMillis()

        val duration = if (instance.status == Instance.STATUS_STARTED) {
            instance.duration + ((currentTime - (instance.activeStartTime ?: currentTime)) / 1000)
        } else {
            instance.duration
        }

        val pauseTime = if (instance.status == Instance.STATUS_PAUSED) {
            instance.totalPause + ((currentTime - (instance.pauseStartTime ?: currentTime)) / 1000)
        } else {
            instance.totalPause
        }

        // Update your UI elements here, e.g., TextViews for duration and pause time
        binding.textViewInstanceStartTime.text = instance.time.toString()
        binding.textViewDuration.text = formatDuration(duration)
        binding.textViewPause.text = formatDuration(pauseTime)
    }

    private fun formatDuration(durationInSeconds: Long): String {
        val duration = Duration.ofSeconds(durationInSeconds)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onResume() {
        super.onResume()
        startUiUpdater() // Start updating the UI when the fragment is visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiUpdateJob?.cancel() // Stop updating the UI when the fragment is destroyed
    }

    // Update the selected instance details
    fun updateSelectedView(instanceWithTask: InstanceWithTask) {
        binding.textViewInstanceStartTime.text = instanceWithTask.instance.time.toString()
        // Update other views as needed
    }



}

