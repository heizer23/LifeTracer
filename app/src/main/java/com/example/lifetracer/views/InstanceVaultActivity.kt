import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracer.R
import com.example.lifetracer.charts.ChartRepository
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceRepository
import com.example.lifetracer.viewModel.InstancesViewModel
import com.example.lifetracer.viewModel.InstancesViewModelFactory
import com.example.lifetracer.views.VaultAdapter

class InstanceVaultActivity : AppCompatActivity() {

    private lateinit var adapter: VaultAdapter


    private val viewModel: InstancesViewModel by viewModels {
        InstancesViewModelFactory(
            instanceRepository = InstanceRepository(
                instanceDao = AppDatabase.getDatabase(applicationContext).instanceDao(),
            ),
            chartRepository = ChartRepository(AppDatabase.getDatabase(applicationContext).chartDataDao())
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instance_vault) // Use setContentView for non-DataBinding activities

        setupRecyclerView()
        loadInstances()
    }


    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.vaultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Assuming viewModel is already initialized and provides a way to fetch instances
        // and has a method to handle the selection/start of an instance.

        // Initialize your VaultAdapter with necessary dependencies.
        adapter = VaultAdapter(lifecycleScope, viewModel, { instance ->
            // This is where you handle the copying of an instance.
            // You might want to show a confirmation dialog, then duplicate the instance and save/update it.
            // For simplicity, let's just log the copy action here.
            Log.d("VaultAdapter", "Copying instance: ${instance.name}")
        }) // Passing a reference to the fetchChartData function

        recyclerView.adapter = adapter

        // Fetch instances and submit them to the adapter.
        // This is a simplified approach. You'd typically observe a LiveData<List<InstanceWithTask>> from your ViewModel.
        viewModel.allActiveInstanceWithTask.observe(this) { instances ->
            adapter.submitList(instances)
        }
    }


    private fun loadInstances() {
        // Fetch instances from your database or viewModel and update the adapter
        // This is a simplified example. In a real app, this would involve fetching data from a ViewModel or Repository.
       // val instances = listOf<InstanceWithTask>() // Get your list of instances here
       // adapter.updateData(instances)
    }
}
