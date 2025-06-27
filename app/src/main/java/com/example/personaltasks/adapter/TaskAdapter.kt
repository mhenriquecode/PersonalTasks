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
                (onTaskClickListener as AppCompatActivity).menuInflater.inflate(R.menu.context_menu, menu)

                // Encontre os itens do menu
                val editTaskItem = menu.findItem(R.id.edit_task)
                val removeTaskItem = menu.findItem(R.id.remove_task)
                val detailsTaskItem = menu.findItem(R.id.details_task)

                if (isDeletedTasksScreen) {
                    // Se estiver na tela de tarefas excluídas:
                    editTaskItem.title = "Reativar Tarefa" // Renomeia "Editar task"
                    removeTaskItem.isVisible = false // Oculta "Remover task"
                } else {
                    // Se estiver na tela principal (ou outra tela não excluída):
                    editTaskItem.title = "Editar task" // Garante o nome original (caso mude)
                    removeTaskItem.isVisible = true // Garante visibilidade
                }

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