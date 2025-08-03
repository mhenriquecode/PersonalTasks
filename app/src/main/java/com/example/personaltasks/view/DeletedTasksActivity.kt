package com.example.personaltasks.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personaltasks.adapter.TaskAdapter
import com.example.personaltasks.controller.MainController
import com.example.personaltasks.databinding.ActivityDeletedTasksBinding
import com.example.personaltasks.model.Constant
import com.example.personaltasks.model.Task
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast

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

        adapter = TaskAdapter(deletedTasks, this, true) // true para tela de excluídas
        binding.rvDeletedTasks.layoutManager = LinearLayoutManager(this)
        binding.rvDeletedTasks.adapter = adapter

        binding.btnBackToMain.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            loadDeletedTasks()
        }, 400) // delay de 400ms
    }

    private fun loadDeletedTasks() {
        controller.getDeletedTasks { fetchedTasks ->
            deletedTasks.clear()
            deletedTasks.addAll(fetchedTasks)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        // Reativar Tarefa
        val task = deletedTasks[position]
        task.isDeleted = false // Marca como não excluída
        controller.updateTask(task) { success -> // Atualiza no Firestore
            if (success) {
                Toast.makeText(this, "Tarefa reativada com sucesso!", Toast.LENGTH_SHORT).show()
                loadDeletedTasks() // Recarrega a lista para refletir a mudança
            } else {
                Toast.makeText(this, "Erro ao reativar tarefa.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        val taskToRemove = deletedTasks[position]
        // O firestoreId é crucial para deletar do Firestore
        taskToRemove.firestoreId?.let { firestoreId ->
            controller.deleteTask(firestoreId) { success ->
                if (success) {
                    Toast.makeText(this, "Tarefa removida permanentemente!", Toast.LENGTH_SHORT).show()
                    loadDeletedTasks() // Recarrega a lista após a exclusão
                } else {
                    Toast.makeText(this, "Erro ao remover tarefa permanentemente.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this, "Erro: ID do Firestore nulo. Não foi possível remover.", Toast.LENGTH_SHORT).show()
        }
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