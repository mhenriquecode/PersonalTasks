package com.example.personaltasks.model

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TaskFirestore(private val context: Context) : TaskDao {

    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")
    private val TAG = "TaskFirestore"

    // Métodos da interface TaskDao (alguns serão adaptados para a assincronicidade)
    override fun createTask(task: Task): Long {
        // Esta implementação síncrona não é ideal para Firestore.
        // A lógica real de adição e manipulação do ID será feita em métodos assíncronos.
        // Por compatibilidade com a interface, um retorno dummy.
        Log.e(TAG, "createTask(Task) síncrono não é recomendado para Firestore. Use métodos assíncronos.")
        return -1L // Indica que a operação não foi concluída de forma síncrona
    }

    override fun retrieveTask(id: Int): Task {
        Log.e(TAG, "retrieveTask(Int) síncrono não é recomendado para Firestore. Use o firestoreId (String) e callbacks.")
        return Task() // Retorno dummy
    }

    override fun retrieveTasks(): MutableList<Task> {
        Log.e(TAG, "retrieveTasks() síncrono não é recomendado para Firestore. Use um método assíncrono.")
        return mutableListOf() // Retorno vazio
    }

    override fun retrieveDeletedTasks(): MutableList<Task> {
        Log.e(TAG, "retrieveDeletedTasks() síncrono não é recomendado para Firestore. Use um método assíncrono.")
        return mutableListOf() // Retorno vazio
    }

    override fun updateTask(task: Task): Int {
        Log.e(TAG, "updateTask(Task) síncrono não é recomendado para Firestore. Use métodos assíncronos.")
        return -1 // Retorno dummy
    }

    override fun deleteTask(id: Int): Int {
        Log.e(TAG, "deleteTask(Int) síncrono não é recomendado para Firestore. Use o firestoreId (String) e métodos assíncronos.")
        return -1 // Retorno dummy
    }
    // --- Métodos Assíncronos para Interação com Firestore ---

    // Adiciona uma nova tarefa ao Firestore
    fun createTaskFirestore(task: Task, onComplete: (Boolean, String?) -> Unit) {
        val taskMap = taskToMap(task)
        tasksCollection.add(taskMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Tarefa adicionada com ID: ${documentReference.id}")
                // O Firestore ID gerado é importante, você pode querer usá-lo
                // para atualizar o objeto 'task' localmente ou passá-lo de volta.
                // Aqui estamos apenas indicando sucesso.
                onComplete(true, documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao adicionar tarefa", e)
                onComplete(false, null)
            }
    }

    // Busca uma tarefa específica pelo ID do documento do Firestore (String)
    fun retrieveTaskByFirestoreId(firestoreId: String, onComplete: (Task?) -> Unit) {
        tasksCollection.document(firestoreId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists() && documentSnapshot.data != null) {
                    val task = documentToTask(documentSnapshot.id, documentSnapshot.data!!)
                    onComplete(task)
                } else {
                    Log.d(TAG, "Nenhum documento encontrado com ID: $firestoreId")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao buscar documento", e)
                onComplete(null)
            }
    }

    // Busca todas as tarefas (não excluídas)
    fun getAllTasks(onComplete: (List<Task>) -> Unit) {
        tasksCollection
            .whereEqualTo("isDeleted", false)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<Task>()
                for (document in querySnapshot.documents) {
                    val task = documentToTask(document.id, document.data!!)
                    taskList.add(task)
                }
                onComplete(taskList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao obter tarefas ativas", e)
                onComplete(emptyList())
            }
    }

    // Busca todas as tarefas excluídas
    fun getDeletedTasks(onComplete: (List<Task>) -> Unit) {
        tasksCollection
            .whereEqualTo("isDeleted", true)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<Task>()
                for (document in querySnapshot.documents) {
                    val task = documentToTask(document.id, document.data!!)
                    taskList.add(task)
                }
                onComplete(taskList)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao obter tarefas excluídas", e)
                onComplete(emptyList())
            }
    }

    // Atualiza uma tarefa existente no Firestore
    fun updateTaskFirestore(task: Task, onComplete: (Boolean) -> Unit) {
        if (task.firestoreId == null) {
            Log.e(TAG, "Erro: firestoreId é nulo ao tentar atualizar a tarefa: ${task.title}")
            onComplete(false)
            return
        }
        val taskMap = taskToMap(task)
        tasksCollection.document(task.firestoreId!!)
            .set(taskMap) // 'set' sobrescreve o documento; 'update' mescla campos
            .addOnSuccessListener {
                Log.d(TAG, "Tarefa atualizada com sucesso: ${task.title}")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao atualizar tarefa", e)
                onComplete(false)
            }
    }

    // Deleta um documento do Firestore permanentemente
    fun deleteDocumentFirestore(firestoreId: String, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(firestoreId).delete()
            .addOnSuccessListener {
                Log.d(TAG, "Documento deletado com sucesso: $firestoreId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao deletar documento: $firestoreId", e)
                onComplete(false)
            }
    }

    // --- Funções de Mapeamento ---
    private fun taskToMap(task: Task): Map<String, Any?> {
        return hashMapOf(
            "title" to task.title,
            "description" to task.description,
            "deadline" to task.deadline,
            "details" to task.details,
            "isDone" to task.isDone,
            "isDeleted" to task.isDeleted,
            "prioridade" to task.prioridade
            // Não inclua o id numérico aqui, ele não é o ID do documento no Firestore
        )
    }

    private fun documentToTask(documentId: String, data: Map<String, Any>): Task {
        return Task(
            // Mapeamento dummy para o campo Int? id original
            id = documentId.hashCode(),
            title = data["title"] as? String ?: "",
            description = data["description"] as? String ?: "",
            deadline = data["deadline"] as? String ?: "",
            details = data["details"] as? String ?: "",
            isDone = data["isDone"] as? Boolean ?: false,
            isDeleted = data["isDeleted"] as? Boolean ?: false,
            prioridade = data["prioridade"] as? String ?: ""
        ).apply {
            firestoreId = documentId // Crucial: armazena o ID real do Firestore
        }
    }
}