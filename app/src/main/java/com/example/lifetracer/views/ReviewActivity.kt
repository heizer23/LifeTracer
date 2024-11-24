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

class ReviewActivity : AppCompatActivity() {

    private lateinit var viewModel: InstancesViewModel
    private lateinit var adapter: ReusableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_instances) // This must come first

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFinishedInstances)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this, InstancesViewModelFactory(
            InstanceRepository(AppDatabase.getDatabase(applicationContext).instanceDao()),
            ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )).get(InstancesViewModel::class.java)

        val currentDate = getCurrentDate()
        adapter = ReusableAdapter(
            scope = lifecycleScope,
            viewModel = viewModel,
            onDeleteInstance = { instance -> viewModel.deleteInstance(instance) },
            onRestoreOrFinishInstance = { instance -> viewModel.moveTaskToMain(instance, true) },
            useVaultLayout = true
        )

        recyclerView.adapter = adapter

        // Set up ItemTouchHelper for drag-and-swipe functionality
        ReusableAdapterUtil.attachItemTouchHelper(recyclerView, adapter)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Observe the LiveData for finished instances
        viewModel.getFinishedInstancesForDay(currentDate).observe(this) { finishedInstances ->
            adapter.submitList(finishedInstances)
        }
    }

}
