package com.example.lifetracer.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.databinding.InstanceDetailBinding
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstanceDetailViewModel
import com.example.lifetracer.viewModel.InstanceDetailViewModelFactory

class InstanceDetailActivity : AppCompatActivity() {

    private lateinit var binding: InstanceDetailBinding
    private lateinit var viewModel: InstanceDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and bind the ViewModel
        binding = InstanceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSave.setOnClickListener {
            val name = viewModel.name.value ?: ""
            val inputType = viewModel.inputType.value ?: -1
            val quantity = viewModel.quantity.value ?: 0.0
            val quality = viewModel.quality.value ?: ""
            val comment = viewModel.comment.value ?: ""
            val regularity = viewModel.regularity.value ?: 0

            viewModel.saveChanges(
                newName = name,
                newInputType = inputType,
                newQuantity = quantity,
                newQuality = quality,
                newComment = comment,
                newRegularity = regularity
            )

            // Optionally, finish the activity after saving
            finish()
        }


        val instanceId = intent.getLongExtra("INSTANCE_ID_EXTRA", -1)
        if (instanceId == -1L) {
            // Handle error
            finish()
            return
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            InstanceDetailViewModelFactory(
                instanceId = intent.getLongExtra("INSTANCE_ID_EXTRA", -1),
                instanceRepository = InstanceRepository(
                    instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao()
                ),
                chartRepository = ChartRepository(
                    chartDataDao = AppDatabase.getDatabase(applicationContext).chartDataDao()
                )
            )
        ).get(InstanceDetailViewModel::class.java)


        // Bind ViewModel to the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

}
