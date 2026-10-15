package com.employeedirectory.app.presentation.employeedirectory.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.employeedirectory.app.EmployeeDirectoryApp
import com.employeedirectory.app.databinding.ActivityMainBinding
import com.employeedirectory.app.domain.employeedirectory.model.Employee
import com.employeedirectory.app.presentation.employeedirectory.detail.EmployeeDetailActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = EmployeeAdapter(onEmployeeClick = ::openEmployeeDetail)

    private val viewModel: EmployeeListViewModel by lazy {
        val repository = (application as EmployeeDirectoryApp).appContainer.employeeRepository
        EmployeeListViewModelFactory(repository)
            .create(EmployeeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.recyclerEmployees.layoutManager = LinearLayoutManager(this)
        binding.recyclerEmployees.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadEmployees() }
        binding.buttonRetry.setOnClickListener { viewModel.loadEmployees() }

        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: EmployeeListUiState) {
        binding.swipeRefresh.isRefreshing = false

        binding.progressLoading.isVisible = state is EmployeeListUiState.Loading
        binding.recyclerEmployees.isVisible = state is EmployeeListUiState.Success
        binding.groupError.isVisible = state is EmployeeListUiState.Error
        binding.textEmptyState.isVisible = state is EmployeeListUiState.Empty

        when (state) {
            is EmployeeListUiState.Success -> adapter.submitList(state.employees)
            is EmployeeListUiState.Error -> binding.textErrorMessage.text = state.message
            EmployeeListUiState.Empty, EmployeeListUiState.Loading -> Unit
        }
    }

    private fun openEmployeeDetail(employee: Employee) {
        val intent = Intent(this, EmployeeDetailActivity::class.java).apply {
            putExtra(EmployeeDetailActivity.EXTRA_NAME, employee.name)
            putExtra(EmployeeDetailActivity.EXTRA_AGE, employee.age ?: EmployeeDetailActivity.NO_VALUE)
            putExtra(EmployeeDetailActivity.EXTRA_SALARY, employee.salary ?: EmployeeDetailActivity.NO_VALUE.toDouble())
        }
        startActivity(intent)
    }
}
