package com.example.personaltasks.controller

import android.content.Context
import com.example.personaltasks.model.Task
import com.example.personaltasks.model.TaskDao
import com.example.personaltasks.model.TaskSqlite

class MainController(context: Context) {
    private val taskDao: TaskDao = TaskSqlite(context)

    fun insertTask(task: Task) = taskDao.createTask(task)
    fun getTask(id: Int) = taskDao.retrieveTask(id)
    fun getAllTasks() = taskDao.retrieveTasks()
    fun getDeletedTasks() = taskDao.retrieveDeletedTasks()
    fun updateTask(task: Task) = taskDao.updateTask(task)

    // Remove logicamente a tarefa, marcando isDeleted = true
    fun removeTask(task: Task): Int {
        val deletedTask = task.copy(isDeleted = true)
        return taskDao.updateTask(deletedTask)
    }
}