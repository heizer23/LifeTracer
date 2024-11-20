package com.example.lifetracer.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.Utilities.getCurrentDate
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import kotlinx.coroutines.launch

class FinishedInstancesActivity : AppCompatActivity() {

    private lateinit var viewModel: InstancesViewModel
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_instances)

        viewModel = ViewModelProvider(this, InstancesViewModelFactory(
            InstanceRepository(AppDatabase.getDatabase(applicationContext).instanceDao()),
            ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )
        ).get(InstancesViewModel::class.java)

        val currentDate = getCurrentDate()
        setupRecyclerView()

        // Observe the LiveData for finished instances
        viewModel.getFinishedInstancesForDay(currentDate).observe(this) { finishedInstances ->
            adapter.submitList(finishedInstances)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFinishedInstances)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReviewAdapter(lifecycleScope, viewModel)
        recyclerView.adapter = adapter
    }
}
