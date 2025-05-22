package com.example.personaltasks.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
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
import com.example.personaltasks.controller.MainController
import com.example.personaltasks.databinding.ActivityMainBinding
import com.example.personaltasks.model.Constant.EXTRA_TASK
import com.example.personaltasks.model.Constant.EXTRA_TASK_POSITION
import com.example.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.personaltasks.model.Task

class MainActivity : AppCompatActivity(), OnTaskClickListener {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var newTaskLauncher: ActivityResultLauncher<Intent>
    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    // Controller com acesso ao banco
    private val mainController: MainController by lazy {
        MainController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(amb.root)
        setSupportActionBar(amb.toolbar)

        taskAdapter = TaskAdapter(taskList, this)
        amb.rvTaks.layoutManager = LinearLayoutManager(this)
        amb.rvTaks.adapter = taskAdapter

        newTaskLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                } else {
                    data?.getParcelableExtra<Task>(EXTRA_TASK)
                }

                val position = data?.getIntExtra(EXTRA_TASK_POSITION, -1) ?: -1

                receivedTask?.let { task ->
                    if (position != -1) {
                        taskList[position] = task
                        taskAdapter.notifyItemChanged(position)
                        mainController.updateTask(task) // Atualiza no banco
                    } else {
                        taskList.add(task)
                        taskAdapter.notifyItemInserted(taskList.size - 1)
                        mainController.insertTask(task) // Insere no banco
                    }
                }
            }
        }
    }
    // Carrega as tarefas do banco sempre que volta a MainActivity
    override fun onResume() {
        super.onResume()
        loadTasksFromDatabase()
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

    override fun onEditTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_TASK_POSITION, position)
        }
        newTaskLauncher.launch(intent)
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        val taskToRemove = taskList[position]
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        mainController.removeTask(taskToRemove.id!!) // Remove do banco
    }

    override fun onDetailsTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra(EXTRA_TASK, taskList[position])
        intent.putExtra(EXTRA_VIEW_TASK, true)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTasksFromDatabase() {
        val dbTasks = mainController.getAllTasks()
        taskList.clear()
        taskList.addAll(dbTasks)
        taskAdapter.notifyDataSetChanged()
    }
}
