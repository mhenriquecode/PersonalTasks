package com.example.personaltasks.controller

import android.content.Context
import android.util.Log
import com.example.personaltasks.model.Task
import com.example.personaltasks.model.TaskDao
import com.example.personaltasks.model.TaskFirestore
import com.example.personaltasks.model.TaskSqlite

class MainController(private val context: Context) {
    // Apenas a instância de TaskFirestore
    private val taskFirestore: TaskFirestore = TaskFirestore(context)

    // Insere uma nova tarefa no Firestore
    fun insertTask(task: Task, onComplete: (Boolean, String?) -> Unit) {
        taskFirestore.createTaskFirestore(task, onComplete)
    }

    // Obtém uma tarefa específica pelo firestoreId (String)
    fun getTask(firestoreId: String, onComplete: (Task?) -> Unit) {
        taskFirestore.retrieveTaskByFirestoreId(firestoreId, onComplete)
    }

    // Obtém todas as tarefas não excluídas
    fun getAllTasks(onComplete: (List<Task>) -> Unit) {
        taskFirestore.getAllTasks(onComplete)
    }

    // Obtém todas as tarefas excluídas
    fun getDeletedTasks(onComplete: (List<Task>) -> Unit) {
        taskFirestore.getDeletedTasks(onComplete)
    }

    // Atualiza uma tarefa existente no Firestore
    fun updateTask(task: Task, onComplete: (Boolean) -> Unit) {
        if (task.firestoreId == null) {
            Log.e("MainController", "Erro: Firestore ID é nulo ao tentar atualizar a tarefa: ${task.title}")
            onComplete(false)
            return
        }
        taskFirestore.updateTaskFirestore(task, onComplete)
    }

    // "Remove" uma tarefa logicamente, marcando-a como isDeleted=true
    fun removeTask(task: Task, onComplete: (Boolean) -> Unit) {
        val deletedTask = task.copy(isDeleted = true) // Cria uma cópia para não alterar o objeto original diretamente
        if (deletedTask.firestoreId != null) {
            taskFirestore.updateTaskFirestore(deletedTask, onComplete)
        } else {
            Log.e("MainController", "Erro: Firestore ID é nulo ao tentar remover tarefa logicamente.")
            onComplete(false)
        }
    }

    // Deleta uma tarefa permanentemente do Firestore
    fun deleteTask(firestoreId: String, onComplete: (Boolean) -> Unit) {
        taskFirestore.deleteDocumentFirestore(firestoreId, onComplete)
    }
}