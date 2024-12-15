package com.example.lifetracer.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lifetracer.databinding.FragmentSelectedBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.SelectedInstanceViewModel
import com.example.lifetracer.viewModel.SelectedInstanceViewModelFactory

class MainSelectedFragment : Fragment() {

    private var _binding: FragmentSelectedBinding? = null
    private val binding get() = _binding!!

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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


