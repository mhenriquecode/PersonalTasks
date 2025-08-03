package com.example.personaltasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltasks.R
import com.example.personaltasks.databinding.TaskItemBinding
import com.example.personaltasks.model.Task
import com.example.personaltasks.view.OnTaskClickListener

// passando uma lista de tasks
class TaskAdapter(private val taskList: List<Task>,
                  private val onTaskClickListener: OnTaskClickListener,
                  private val isDeletedTasksScreen: Boolean = false
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    inner class TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.txtTitle.text = task.title
            binding.txtDescription.text = task.description
            binding.txtDate.text = task.deadline
            binding.txtIsdone.isChecked = task.isDone
        }
        init {
            // Clique longo → mostra menu de contexto
            binding.root.setOnCreateContextMenuListener { menu, _, _ ->
                val menuInflater = (onTaskClickListener as AppCompatActivity).menuInflater

                if (isDeletedTasksScreen) {
                    // Infla o menu específico para tarefas excluídas
                    menuInflater.inflate(R.menu.context_menu_deleted_tasks, menu)

                    // Encontra os itens do novo menu
                    val reactivateTaskItem = menu.findItem(R.id.reactivate_task)
                    val detailsTaskDeletedItem = menu.findItem(R.id.details_task_deleted)
                    val deletePermanentlyTaskItem = menu.findItem(R.id.delete_permanently_task)

                    // Define os listeners para os itens do novo menu
                    reactivateTaskItem.setOnMenuItemClickListener {
                        onTaskClickListener.onEditTaskMenuItemClick(adapterPosition) // Reutilizamos onEdit para reativar
                        true
                    }
                    detailsTaskDeletedItem.setOnMenuItemClickListener {
                        onTaskClickListener.onDetailsTaskMenuItemClick(adapterPosition)
                        true
                    }
                    deletePermanentlyTaskItem.setOnMenuItemClickListener {
                        onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition) // Reutilizamos onRemove para exclusão permanente
                        true
                    }

                } else {
                    // Infla o menu original para a tela principal
                    menuInflater.inflate(R.menu.context_menu, menu)

                    // Encontre os itens do menu original
                    val editTaskItem = menu.findItem(R.id.edit_task)
                    val removeTaskItem = menu.findItem(R.id.remove_task)
                    val detailsTaskItem = menu.findItem(R.id.details_task)

                    // Define os listeners para os itens do menu original
                    editTaskItem.setOnMenuItemClickListener {
                        onTaskClickListener.onEditTaskMenuItemClick(adapterPosition)
                        true
                    }
                    removeTaskItem.setOnMenuItemClickListener {
                        onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                        true
                    }
                    detailsTaskItem.setOnMenuItemClickListener {
                        onTaskClickListener.onDetailsTaskMenuItemClick(adapterPosition)
                        true
                    }
                }
            }
        }
    }

    // criando e inflando
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // preenche a taks da posição com os dados
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    // conta as tasks
    override fun getItemCount(): Int = taskList.size

}