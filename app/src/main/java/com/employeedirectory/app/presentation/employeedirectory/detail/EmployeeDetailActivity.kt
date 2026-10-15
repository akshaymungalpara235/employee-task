package com.employeedirectory.app.presentation.employeedirectory.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.employeedirectory.app.databinding.ActivityEmployeeDetailBinding
import com.employeedirectory.app.presentation.common.CurrencyFormatter

class EmployeeDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_employee_name"
        const val EXTRA_AGE = "extra_employee_age"
        const val EXTRA_SALARY = "extra_employee_salary"

        /** Sentinel for "field was missing/null" since Intent extras can't carry nullable primitives. */
        const val NO_VALUE = -1
    }

    private lateinit var binding: ActivityEmployeeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val age = intent.getIntExtra(EXTRA_AGE, NO_VALUE)
        val salary = intent.getDoubleExtra(EXTRA_SALARY, NO_VALUE.toDouble())

        binding.textDetailName.text = name
        binding.textDetailAgeValue.text = if (age == NO_VALUE) "N/A" else age.toString()
        binding.textDetailSalaryValue.text =
            if (salary == NO_VALUE.toDouble()) "N/A" else CurrencyFormatter.format(salary)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
