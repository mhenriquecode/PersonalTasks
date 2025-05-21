package com.example.personaltasks.view

interface OnTaskClickListener {
    fun onTaskClick(position: Int)
    fun onEditTaskMenuItemClick(position: Int)
    fun onRemoveTaskMenuItemClick(position: Int)
    fun onDetailsTaskMenuItemClick(position: Int)
}