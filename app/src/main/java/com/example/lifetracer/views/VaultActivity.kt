package com.example.lifetracer.views

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifetracer.R
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.databinding.ActivityInstanceVaultBinding
import kotlinx.coroutines.launch

class VaultActivity : AppCompatActivity(), RecyclerViewFragment.OnInstanceSelectedListener {

    private lateinit var binding: ActivityInstanceVaultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityInstanceVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.recyclerViewContainer, RecyclerViewFragment())
                .replace(R.id.detailViewContainer, MainSelectedFragment())
                .commitNow()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonAddInstance.setOnClickListener {
            val taskCreationFragment = TaskCreationFragment.newInstance().apply {
                setTaskCreationListener(object : TaskCreationFragment.TaskCreationListener {
                    override fun onInstanceCreated(subTask: InstanceWithTask) {
                        lifecycleScope.launch {
                            viewModel.addInstance(subTask)
                        }
                    }
                })
            }
            taskCreationFragment.show(supportFragmentManager, "TaskCreationFragment")
    }
    }

    override fun onInstanceSelected(instance: InstanceWithTask) {
        val detailFragment = supportFragmentManager.findFragmentById(R.id.detailViewContainer) as? MainSelectedFragment
        detailFragment?.updateUi(instance)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
