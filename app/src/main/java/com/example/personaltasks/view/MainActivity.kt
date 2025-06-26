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
import com.google.firebase.Firebase

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
        setContentView(amb.root) // Define a visualização da tela
        setSupportActionBar(amb.toolbar) // Usa a toolbar personalizada

        // Inicializa o adaptador e configura o RecyclerView
        taskAdapter = TaskAdapter(taskList, this)
        amb.rvTaks.layoutManager = LinearLayoutManager(this)
        amb.rvTaks.adapter = taskAdapter

        // Define o que acontece quando a TaskActivity retorna um resultado
        newTaskLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Recupera a tarefa enviada de volta pela TaskActivity
                val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                } else {
                    data?.getParcelableExtra<Task>(EXTRA_TASK)
                }

                // Recupera a posição da tarefa (se for edição)
                val position = data?.getIntExtra(EXTRA_TASK_POSITION, -1) ?: -1

                // Se for uma tarefa válida, adiciona ou atualiza na lista e no banco
                receivedTask?.let { task ->
                    if (position != -1) {
                        // Atualiza tarefa existente
                        taskList[position] = task
                        taskAdapter.notifyItemChanged(position)
                        mainController.updateTask(task)
                    } else {
                        // Adiciona nova tarefa
                        taskList.add(task)
                        taskAdapter.notifyItemInserted(taskList.size - 1)
                        mainController.insertTask(task)
                    }
                }
            }
        }
        Firebase
    }

    // Recarrega as tarefas do banco sempre que a tela principal volta a aparecer
    override fun onResume() {
        super.onResume()
        loadTasksFromDatabase()
    }

    // Cria o menu superior com a opção de adicionar nova tarefa
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Trata o clique no item do menu (nova tarefa)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_task_menu -> {
                val intent = Intent(this, TaskActivity::class.java)
                newTaskLauncher.launch(intent) // Abre a tela de nova tarefa
                true
            }
            else -> false
        }
    }

    // Quando o usuário escolhe "Editar" em uma tarefa
    override fun onEditTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_TASK_POSITION, position)
        }
        newTaskLauncher.launch(intent) // Abre a tela de edição de tarefa
    }

    // Quando o usuário escolhe "Remover" em uma tarefa
    override fun onRemoveTaskMenuItemClick(position: Int) {
        val taskToRemove = taskList[position]
        taskList.removeAt(position) // Remove da lista
        taskAdapter.notifyItemRemoved(position)
        mainController.removeTask(taskToRemove.id!!) // Remove do banco
    }

    // Quando o usuário escolhe "Detalhes" em uma tarefa
    override fun onDetailsTaskMenuItemClick(position: Int) {
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra(EXTRA_TASK, taskList[position])
        intent.putExtra(EXTRA_VIEW_TASK, true) // Modo de visualização apenas
        startActivity(intent)
    }

    // Carrega todas as tarefas do banco de dados e mostra no RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    private fun loadTasksFromDatabase() {
        val dbTasks = mainController.getAllTasks()
        taskList.clear()
        taskList.addAll(dbTasks)
        taskAdapter.notifyDataSetChanged() // Atualiza a tela
    }
}
