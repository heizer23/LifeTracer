package com.example.lifetracer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R

class MainActivity : AppCompatActivity() {

    private lateinit var instanceRecyclerView: RecyclerView
    private lateinit var instanceAdapter: InstanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Controller.initialize(applicationContext)
        // Initialize the RecyclerView for instances
        instanceRecyclerView = findViewById(R.id.recyclerViewInstances)
        instanceRecyclerView.layoutManager = LinearLayoutManager(this)
        instanceAdapter = InstanceAdapter(this, getDummyInstances()) // Replace with actual instances
        instanceRecyclerView.adapter = instanceAdapter


        // Find the button by its ID
        val buttonGoToManageTasks = findViewById<View>(R.id.buttonGoToManageTasks)

        // Set a click listener for the button
        buttonGoToManageTasks.setOnClickListener {
            // Create an intent to open the ManageTasksActivity
            val intent = Intent(this, ManageTasksActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateInstanceList()

    }

    private fun updateInstanceList() {
        val instances = Controller.getAllInstances()
        instanceAdapter.updateList(instances)
    }

    private fun getDummyInstances(): List<Instance> {
        // Return a list of dummy instances here or fetch real data from the database
        val dummyInstances = mutableListOf<Instance>()
        // Add instances to the list
        return dummyInstances
    }
}