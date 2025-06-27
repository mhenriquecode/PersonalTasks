package com.example.personaltasks.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personaltasks.R
import com.example.personaltasks.adapter.TaskAdapter
import com.example.personaltasks.controller.MainController
import com.example.personaltasks.databinding.ActivityMainBinding
import com.example.personaltasks.model.Constant
import com.example.personaltasks.model.Task
class MainActivity : AppCompatActivity(), OnTaskClickListener {

    private lateinit var amb: ActivityMainBinding
    private lateinit var mainController: MainController
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    private lateinit var newTaskLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        amb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbar)

        mainController = MainController(this)

        // Passamos 'false' para indicar que não é a tela de tarefas excluídas
        taskAdapter = TaskAdapter(taskList, this, false)
        amb.rvTaks.layoutManager = LinearLayoutManager(this)
        amb.rvTaks.adapter = taskAdapter

        // Configurar o launcher para adicionar/editar tarefas
        newTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val task = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        data.getParcelableExtra(Constant.EXTRA_TASK, Task::class.java)
                    } else {
                        data.getParcelableExtra<Task>(Constant.EXTRA_TASK)
                    }
                    val isEditing = data.getBooleanExtra(Constant.EXTRA_IS_EDITING, false) // Novo extra para saber se é edição

                    task?.let { newTask ->
                        if (isEditing) { // Se for edição, o firestoreId já deve estar na tarefa
                            mainController.updateTask(newTask) { success ->
                                if (success) {
                                    Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                                    loadTasks() // Recarrega a lista
                                } else {
                                    Toast.makeText(this, "Erro ao atualizar tarefa.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // Se for nova tarefa, Firestore vai gerar um ID
                            mainController.insertTask(newTask) { success, firestoreId ->
                                if (success) {
                                    // Se precisar do firestoreId na UI imediatamente
                                    // newTask.firestoreId = firestoreId
                                    Toast.makeText(this, "Tarefa adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                                    loadTasks() // Recarrega a lista
                                } else {
                                    Toast.makeText(this, "Erro ao adicionar tarefa.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        amb.btnDeletedTasks.setOnClickListener {
            startActivity(Intent(this, DeletedTasksActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks() // Carrega as tarefas sempre que a MainActivity for resumida
    }

    private fun loadTasks() {
        mainController.getAllTasks { fetchedTasks ->
            taskList.clear()
            taskList.addAll(fetchedTasks)
            taskAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_task_menu -> {
                newTaskLauncher.launch(Intent(this, TaskActivity::class.java).apply {
                    putExtra(Constant.EXTRA_IS_EDITING, false) // Nova tarefa
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, taskList[position])
            // Não precisa mais de EXTRA_TASK_POSITION se você vai usar o firestoreId para update
            putExtra(Constant.EXTRA_IS_EDITING, true) // Indica que é uma edição
        }
        newTaskLauncher.launch(intent)
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        val taskToRemove = taskList[position]
        // Usa o firestoreId para a remoção lógica (marcar como isDeleted)
        taskToRemove.firestoreId?.let { firestoreId ->
            mainController.removeTask(taskToRemove) { success ->
                if (success) {
                    Toast.makeText(this, "Tarefa movida para a lixeira!", Toast.LENGTH_SHORT).show()
                    loadTasks() // Recarrega a lista para atualizar a UI
                } else {
                    Toast.makeText(this, "Erro ao mover tarefa para lixeira.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: Toast.makeText(this, "Erro: ID do Firestore nulo para remoção.", Toast.LENGTH_SHORT).show()
    }

    override fun onDetailsTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(Constant.EXTRA_TASK, taskList[position])
            putExtra(Constant.EXTRA_VIEW_TASK, true)
        }
        startActivity(intent)
    }
}