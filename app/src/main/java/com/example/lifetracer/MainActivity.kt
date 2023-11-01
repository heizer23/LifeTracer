package com.example.lifetracer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R

class MainActivity : AppCompatActivity() {

    private lateinit var instanceRecyclerView: RecyclerView
    private lateinit var instanceAdapter: InstanceAdapter
    private lateinit var slideDownAnimation: Animation
    private lateinit var selectedInstanceView: View
    private lateinit var textViewInstanceName: TextView

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
        selectedInstanceView = findViewById<View>(R.id.selectedInstanceView) // Replace with the actual ID

        // Load the slide-down animation
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)


        // Set a click listener for the button
        buttonGoToManageTasks.setOnClickListener {
            // Create an intent to open the ManageTasksActivity
            val intent = Intent(this, ManageTasksActivity::class.java)
            startActivity(intent)
        }

        // here we add the onclicklistener to the Listitems
        instanceAdapter.onItemClickListener = { instance ->
            // Update the selected view with the clicked item's information
            updateSelectedView(instance)
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

    private fun updateSelectedView(instance: Instance) {
        // Update the selected view with the information from the clicked instance
        // You can access and set the views in the selected view layout here.
        val selectedInstanceView = findViewById<View>(R.id.selectedInstanceView) // Replace with the actual ID
        val textViewInstanceName = selectedInstanceView.findViewById<TextView>(R.id.selectedInstanceName)

        textViewInstanceName.text = instance.taskName

        // Show the selected view if it's not already visible
        if (selectedInstanceView.visibility != View.VISIBLE) {
            selectedInstanceView.visibility = View.VISIBLE
            selectedInstanceView.startAnimation(slideDownAnimation)
        }
    }

    private fun toogleSelectedView() {
        if (selectedInstanceView.visibility == View.VISIBLE) {
            // Hide the selected view with animation
            selectedInstanceView.visibility = View.GONE
            selectedInstanceView.startAnimation(slideDownAnimation)
        } else {
            // Show the selected view with animation
            selectedInstanceView.visibility = View.VISIBLE
            selectedInstanceView.startAnimation(slideDownAnimation)
        }
    }
}