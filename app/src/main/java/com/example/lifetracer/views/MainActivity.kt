package com.example.lifetracer.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.data.Instance
import com.example.lifetracer.R
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracer.ViewModel.InstancesViewModel
import com.example.lifetracer.ViewModel.InstancesViewModelFactory
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository

class MainActivity : AppCompatActivity() {

    private lateinit var instanceRecyclerView: RecyclerView
    private lateinit var instanceAdapter: InstanceAdapter
    private lateinit var slideDownAnimation: Animation
    private lateinit var selectedInstanceFragment: SelectedInstanceFragment
    private lateinit var viewModel: InstancesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the singleton database instance and Daos
        val database = AppDatabase.getDatabase(applicationContext)

        val instanceDao = database.instanceDao()
        val taskDao = database.taskDao()
        val instanceRepository = InstanceRepository(instanceDao, taskDao)

        // Create ViewModel
        val factory = InstancesViewModelFactory(instanceRepository)
        viewModel = ViewModelProvider(this, factory).get(InstancesViewModel::class.java)

        setContentView(R.layout.activity_main)

        // Initialize the RecyclerView for instances
        instanceRecyclerView = findViewById(R.id.recyclerViewInstances)
        instanceRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize your adapter with an empty list or initial state
        instanceAdapter = InstanceAdapter(emptyList())
        instanceRecyclerView.adapter = instanceAdapter

        // Observe LiveData from ViewModel and update the adapter
        viewModel.getAllInstances().observe(this, Observer { returnedInstancesTasks ->
            // Update your adapter's data
            instanceAdapter.updateList(returnedInstancesTasks)
        })


        // Find the button by its ID
        val buttonGoToManageTasks = findViewById<View>(R.id.buttonGoToManageTasks)

        // Load the slide-down animation
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        // Create the com.example.lifetracer.fragments.SelectedInstanceFragment
        selectedInstanceFragment = SelectedInstanceFragment()

        // Set a click listener for the button
        buttonGoToManageTasks.setOnClickListener {
            // Create an intent to open the ManageTasksActivity
            val intent = Intent(this, ManageTasksActivity::class.java)
            startActivity(intent)
        }

        // here we add the onclicklistener to the List items
        instanceAdapter.onItemClickListener = { instance ->
            // Update the selected view with the clicked item's information
            selectedInstanceFragment.updateSelectedView(instance)
        }


        // Attach the fragment to the activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.selectedInstanceContainer, selectedInstanceFragment)
            .commit()

    }
}
