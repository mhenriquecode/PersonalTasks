package com.example.personaltasks.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personaltasks.adapter.TaskAdapter
import com.example.personaltasks.controller.MainController
import com.example.personaltasks.databinding.ActivityDeletedTasksBinding
import com.example.personaltasks.model.Constant
import com.example.personaltasks.model.Task

class DeletedTasksActivity : AppCompatActivity(), OnTaskClickListener {

    private lateinit var binding: ActivityDeletedTasksBinding
    private lateinit var controller: MainController
    private lateinit var adapter: TaskAdapter
    private val deletedTasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeletedTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = MainController(this)

        adapter = TaskAdapter(deletedTasks, this, true)
        binding.rvDeletedTasks.layoutManager = LinearLayoutManager(this)
        binding.rvDeletedTasks.adapter = adapter

        binding.btnBackToMain.setOnClickListener {
            setResult(Activity.RESULT_CANCELED) // apenas para indicar que a operação foi "cancelada"
            finish() // Fecha a DeletedTasksActivity e retorna para a MainActivity
        }
    }

    override fun onResume() {
        super.onResume()
        // Atualiza a lista e notifica o adapter, sem recriar
        deletedTasks.clear()
        deletedTasks.addAll(controller.getDeletedTasks())
        adapter.notifyDataSetChanged()
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        // Reativar a tarefa (seta isDeleted como false)
        val task = deletedTasks[position]
        task.isDeleted = false
        controller.updateTask(task)

        // Atualiza a lista após reativar
        deletedTasks.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        //exclusão definitiva, chamaria controller.deleteTask(task.id!!) aqui
    }

    override fun onDetailsTaskMenuItemClick(position: Int) {
        val task = deletedTasks[position]
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, task)
            putExtra(Constant.EXTRA_VIEW_TASK, true)
        }
        startActivity(intent)
    }
}