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
class MainActivity : AppCompatActivity(), SelectedInstanceFragment.SelectedInstanceListener {

    private lateinit var instanceRecyclerView: RecyclerView
    private lateinit var instanceAdapter: InstanceAdapter
    private lateinit var slideDownAnimation: Animation
    private lateinit var selectedInstanceFragment: SelectedInstanceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Controller.initialize(applicationContext)

        // Initialize the RecyclerView for instances
        instanceRecyclerView = findViewById(R.id.recyclerViewInstances)
        instanceRecyclerView.layoutManager = LinearLayoutManager(this)
        instanceAdapter = InstanceAdapter(this, getDummyInstances())
        instanceRecyclerView.adapter = instanceAdapter

        // Find the button by its ID
        val buttonGoToManageTasks = findViewById<View>(R.id.buttonGoToManageTasks)

        // Load the slide-down animation
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        // Create the SelectedInstanceFragment
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

    //    toggleSelectedInstanceFragment()
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

    // Handle button click for "Start" in the fragment
    override fun onStartButtonClicked(instance: Instance) {
        // Handle the "Start" action here
    }

    // Handle button click for "Pause" in the fragment
    override fun onPauseButtonClicked(instance: Instance) {
        // Handle the "Pause" action here
    }

    // Show or hide the selected instance fragment
    private fun toggleSelectedInstanceFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (selectedInstanceFragment.isAdded) {
            fragmentTransaction.hide(selectedInstanceFragment)
        } else {
            fragmentTransaction.show(selectedInstanceFragment)
        }
        fragmentTransaction.commit()
    }
}
