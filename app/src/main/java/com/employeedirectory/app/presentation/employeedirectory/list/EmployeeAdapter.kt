package com.employeedirectory.app.presentation.employeedirectory.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.employeedirectory.app.databinding.ItemEmployeeBinding
import com.employeedirectory.app.domain.employeedirectory.model.Employee
import com.employeedirectory.app.presentation.common.CurrencyFormatter

class EmployeeAdapter(
    private val onEmployeeClick: (Employee) -> Unit
) : ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding, onEmployeeClick)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EmployeeViewHolder(
        private val binding: ItemEmployeeBinding,
        private val onEmployeeClick: (Employee) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.textEmployeeName.text = employee.name
            binding.textEmployeeMeta.text = binding.root.context.getString(
                com.employeedirectory.app.R.string.employee_meta_format,
                employee.age?.toString() ?: "N/A",
                CurrencyFormatter.format(employee.salary)
            )
            binding.root.setOnClickListener { onEmployeeClick(employee) }
        }
    }

    // The API exposes no stable employee id, so full value equality is the only safe
    // way to identify a row; duplicate-looking entries are simply treated as distinct items.
    private object EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean =
            oldItem == newItem
    }
}
