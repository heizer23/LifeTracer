package com.example.lifetracer.views

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lifetracer.R
import com.example.lifetracer.databinding.FragmentSelectedBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.SelectedInstanceViewModel
import com.example.lifetracer.viewModel.SelectedInstanceViewModelFactory

class MainSelectedFragment : Fragment() {

    private var _binding: FragmentSelectedBinding? = null
    private val binding get() = _binding!!
    private lateinit var gestureDetector: GestureDetectorCompat

    private val selectedInstanceViewModel: SelectedInstanceViewModel by activityViewModels {
        SelectedInstanceViewModelFactory(
            InstanceRepository(
                AppDatabase.getDatabase(requireContext()).instanceDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = selectedInstanceViewModel

        setupGestureDetection(binding.mainView) // Now correctly refers to a ConstraintLayout
        return binding.root
    }

    private fun setupGestureDetection(view: View) {
        val gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null || e2 == null) return false
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(e2.y - e1.y)) { // Horizontal swipe
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        return true
                    }
                }
                return false
            }
        })

        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }


    private fun onSwipeLeft() {
        val instance = selectedInstanceViewModel.selectedInstance.value
        if (instance != null) {
            selectedInstanceViewModel.swipeLeftAction(instance)
            Toast.makeText(context, "Swiped Left: Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSwipeRight() {
        val instance = selectedInstanceViewModel.selectedInstance.value
        if (instance != null) {
            selectedInstanceViewModel.swipeRightAction(instance)
            Toast.makeText(context, "Swiped Right: Task Finished", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
