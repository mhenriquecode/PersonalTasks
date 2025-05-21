package com.example.personaltasks.view

interface OnTaskClickListener {
    fun onEditTaskMenuItemClick(position: Int)
    fun onRemoveTaskMenuItemClick(position: Int)
    fun onDetailsTaskMenuItemClick(position: Int)
}