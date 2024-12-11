package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.ListViewModel
import com.example.lifetracer.viewModel.ListViewModelFactory
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {

    private lateinit var adapter: DragAdapter

    private val listViewModel: ListViewModel by viewModels {
        ListViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao(),
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test) // Ensure this layout exists and includes a RecyclerView with id "testRecyclerView"
        Log.d("TestActivity", "onCreate called")

        // Initialize RecyclerView and Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.testRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DragAdapter(
            scope = lifecycleScope,
            onDragEnd = { updatedList ->listViewModel.updatePriorities(updatedList)},
            onDeleteInstance = { instance -> listViewModel.deleteInstance(instance) },
            onRestoreOrFinishInstance = { instance -> listViewModel.moveTaskToMain(instance, false) },
            onCircleClick = { instance ->
                val intent = Intent(this, ActivitySubTask::class.java)
                intent.putExtra("PARENT_TASK_ID", instance.id) // Pass the parent task ID
                startActivity(intent)
            },
        )

        recyclerView.adapter = adapter

        // Set up ItemTouchHelper for drag-and-drop functionality
        setupItemTouchHelper(recyclerView)

        // Load instances into the RecyclerView
        loadInstances()
    }

    private fun loadInstances() {
        listViewModel.instances.observe(this) { instances ->
            lifecycleScope.launch {
                adapter.setData(instances) // Use setData to populate the adapter
            }
        }
    }

    private fun setupItemTouchHelper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0) // No swipe actions
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
                // No swipe functionality
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                adapter.onDragEnded() // Notify the adapter when dragging ends
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }



}
