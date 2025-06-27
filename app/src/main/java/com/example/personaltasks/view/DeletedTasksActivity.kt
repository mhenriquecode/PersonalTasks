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
}