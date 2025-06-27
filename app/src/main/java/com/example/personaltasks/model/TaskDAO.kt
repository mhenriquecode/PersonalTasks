package com.example.personaltasks.model

interface TaskDao {
    fun createTask(task: Task): Long
    fun retrieveTask(id: Int): Task
    fun retrieveTasks(): MutableList<Task>
    fun retrieveDeletedTasks(): MutableList<Task>
    fun updateTask(task: Task): Int
    fun deleteTask(id: Int): Int
}
