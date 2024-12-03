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
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.FragmentSelectedInstanceBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration

class MainSelectedFragment : Fragment() {
    private lateinit var binding: FragmentSelectedInstanceBinding
    private val viewModel: InstancesViewModel by activityViewModels()

    enum class Mode {
        INSTANCES, SUBTASKS
    }

    private var mode: Mode = Mode.INSTANCES // Default to instances
    private var parentTaskId: Long? = null

    private lateinit var chartManager: ChartManager

    // Job is for updating duration and pause
    private var uiUpdateJob: Job? = null

    companion object {
        private const val ARG_MODE = "MODE"
        private const val ARG_PARENT_TASK_ID = "PARENT_TASK_ID"

        fun newInstance(mode: Mode, parentTaskId: Long? = null): MainSelectedFragment {
            val fragment = MainSelectedFragment()
            val args = Bundle().apply {
                putSerializable(ARG_MODE, mode)
                parentTaskId?.let { putLong(ARG_PARENT_TASK_ID, it) }
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getSerializable(ARG_MODE) as Mode
            parentTaskId = it.getLong(ARG_PARENT_TASK_ID, -1L).takeIf { id -> id != -1L }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedInstanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (mode) {
            Mode.INSTANCES -> {
                viewModel.setModeAndParentId(-1)

            }
            Mode.SUBTASKS -> {
                parentTaskId?.let { id ->
                    viewModel.setModeAndParentId(id)
                }
            }
        }

        viewModel.instanceWithLowestPrio.observe(viewLifecycleOwner) { instanceWithTask ->
            instanceWithTask?.let {
                updateSelectedView(it)
                binding.instanceWithTask = it
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonFinish.setOnClickListener {
            val qualityInput = binding.editTextQuality.text.toString()
            val quantityInput = binding.editTextQuantity.text.toString()

            val currentTask = viewModel.instanceWithLowestPrio.value

            if (currentTask != null && viewModel.canFinishInstance(currentTask, qualityInput, quantityInput)) {
                viewModel.finishActiveInstance(qualityInput, quantityInput)
            } else {
                Toast.makeText(context, "Please fill in the required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startUiUpdater() {
        uiUpdateJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val instanceWithTask = viewModel.instanceWithLowestPrio.value
                instanceWithTask?.let {
                    updateUi(it)
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun updateUi(instance: InstanceWithTask) {
        val currentTime = System.currentTimeMillis()

        val duration = if (instance.status == InstanceWithTask.STATUS_STARTED) {
            instance.duration + ((currentTime - (instance.activeStartTime ?: currentTime)) / 1000)
        } else {
            instance.duration
        }

        val pauseTime = if (instance.status == InstanceWithTask.STATUS_PAUSED) {
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
    private fun updateSelectedView(instanceWithTask: InstanceWithTask) {
        binding.textViewInstanceStartTime.text = instanceWithTask.time.toString()
        // Update other views as needed
    }
}

