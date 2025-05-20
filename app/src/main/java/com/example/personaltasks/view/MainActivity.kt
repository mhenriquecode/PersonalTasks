package com.example.personaltasks.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personaltasks.R
import com.example.personaltasks.adapter.TaskAdapter
import com.example.personaltasks.databinding.ActivityMainBinding
import com.example.personaltasks.model.Task

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var newTaskLauncher: ActivityResultLauncher<Intent>
    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(amb.root)
        setSupportActionBar(amb.toolbar)

        // Inicializa o adapter com a lista mutável
        taskAdapter = TaskAdapter(taskList)
        amb.rvTaks.layoutManager = LinearLayoutManager(this)
        amb.rvTaks.adapter = taskAdapter

        // Launcher para adicionar nova tarefa
        newTaskLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val title = it.getStringExtra("TASK_TITLE") ?: ""
                    val description = it.getStringExtra("TASK_DESCRIPTION") ?: ""
                    val deadline = it.getStringExtra("TASK_DEADLINE") ?: ""

                    val newTask = Task(title, description, deadline)
                    taskList.add(newTask)
                    taskAdapter.notifyItemInserted(taskList.size - 1)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_task_menu -> {
                val intent = Intent(this, TaskActivity::class.java)
                newTaskLauncher.launch(intent)
                true
            }
            else -> false
        }
    }

}