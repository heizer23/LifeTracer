package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.ListViewModel
import com.example.lifetracer.viewModel.ListViewModelFactory
import com.example.lifetracer.viewModel.SelectedInstanceViewModel
import com.example.lifetracer.viewModel.SelectedInstanceViewModelFactory
import kotlinx.coroutines.launch

class RecyclerViewFragment : Fragment() {

    private lateinit var adapter: DragAdapter
    private lateinit var recyclerView: RecyclerView

    private val listViewModel: ListViewModel by activityViewModels {
        ListViewModelFactory(
            InstanceRepository(
                AppDatabase.getDatabase(requireContext()).instanceDao()
            )
        )
    }

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
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = DragAdapter(
            scope = lifecycleScope,
            onDragEnd = { updatedList -> listViewModel.updatePriorities(updatedList) },
            onDeleteInstance = { instance -> listViewModel.deleteInstance(instance) },
            onRestoreOrFinishInstance = { instance -> listViewModel.moveTaskToMain(instance, false) },
            onCircleClick = { instance ->
                // Navigate to ActivitySubTask
                val intent = Intent(requireContext(), ActivitySubTask::class.java).apply {
                    putExtra("PARENT_TASK_ID", instance.id) // Pass the parent task ID
                }
                startActivity(intent)
            },
            onItemClick = { instance ->
                selectedInstanceViewModel.setSelectedInstance(instance)
            }
        )





        recyclerView.adapter = adapter

        // Set up ItemTouchHelper for drag-and-swipe functionality
        setupItemTouchHelper()

        // Load instances into the RecyclerView
        loadInstances()
    }

    private fun loadInstances() {
        listViewModel.instances.observe(viewLifecycleOwner) { instances ->
            lifecycleScope.launch {
                adapter.setData(instances)
            }
        }
    }


    private fun setupItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val instance = adapter.getItemAt(position)

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        adapter.onDeleteInstance(instance)
                        Toast.makeText(requireContext(), "Instance deleted", Toast.LENGTH_SHORT).show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        adapter.onRestoreOrFinishInstance(instance)
                        Toast.makeText(requireContext(), "Task restored or finished", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                adapter.onDragEnded()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    interface OnInstanceSelectedListener {
        fun onInstanceSelected(instance: InstanceWithTask)
    }
}
