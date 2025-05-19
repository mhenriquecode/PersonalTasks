package com.example.personaltasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltasks.databinding.TaskItemBinding
import com.example.personaltasks.model.Task

// passando uma lista de tasks
class TaskAdapter(private val taskList: List<Task>) :
RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    // criando viewHolder interno ao Adapter para pegar as task items
    inner class TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // preenchendo a task
        fun bind(task: Task) {
            binding.txtTitle.text = task.title
            binding.txtDescription.text = task.description
            binding.txtDate.text = task.deadline
        }
    }

    // criando e inflando
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // preenche a taks da posição com os dados
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    // conta as tasks
    override fun getItemCount(): Int = taskList.size

}