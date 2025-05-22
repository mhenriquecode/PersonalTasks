package com.example.personaltasks.controller

import com.example.personaltasks.model.Task
import com.example.personaltasks.model.TaskDao
import com.example.personaltasks.model.TaskSqlite
import com.example.personaltasks.view.MainActivity

class MainController(mainActivity: MainActivity) {
    private val taskDao: TaskDao = TaskSqlite(mainActivity)

    fun insertTask(task: Task) = taskDao.createTask(task)
    fun getTask(id: Int) = taskDao.retrieveTask(id)
    fun getAllTasks() = taskDao.retrieveTasks()
    fun updateTask(task: Task) = taskDao.updateTask(task)
    fun removeTask(id: Int) = taskDao.deleteTask(id)
}
