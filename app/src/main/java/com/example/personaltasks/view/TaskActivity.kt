package com.example.personaltasks.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.personaltasks.databinding.ActivityTaskBinding
import com.example.personaltasks.model.Constant
import com.example.personaltasks.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskActivity : AppCompatActivity() {

    // Faz o vínculo com a interface gráfica (ViewBinding)
    private lateinit var binding: ActivityTaskBinding
    private var selectedTask: Task? = null
    private var isViewMode: Boolean = false
    private var isEditing: Boolean = false // Nova flag para indicar modo de edição

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater) // Ajuste para o nome correto do seu layout
        setContentView(binding.root)

        // Recebe a tarefa se estiver em modo de edição ou visualização
        selectedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Constant.EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra<Task>(Constant.EXTRA_TASK)
        }
        isViewMode = intent.getBooleanExtra(Constant.EXTRA_VIEW_TASK, false)
        // Se selectedTask não for nulo, é uma edição ou visualização
        isEditing = selectedTask != null // Automaticamente true se há uma tarefa

        if (selectedTask != null) {
            binding.etTitle.setText(selectedTask!!.title)
            binding.etDescription.setText(selectedTask!!.description)
            binding.etDeadline.setText(selectedTask!!.deadline)
            binding.etDetails.setText(selectedTask!!.details)
        }

        if (isViewMode) {
            // Desativa a edição dos campos
            binding.etTitle.isEnabled = false
            binding.etDescription.isEnabled = false
            binding.etDeadline.isEnabled = false
            binding.etDetails.isEnabled = false
            binding.btnSave.visibility = android.view.View.GONE // Esconde o botão Salvar
            binding.btnCancel.text = "Voltar" // Muda o texto para Voltar
        }

        binding.etDeadline.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSave.setOnClickListener {
            saveTask()
        }

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            // Verifica se a data selecionada não é anterior à data atual
            if (selectedDate.timeInMillis >= Calendar.getInstance().timeInMillis - (1000 * 60 * 60 * 24)) { // Permite data de hoje
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDeadline.setText(dateFormat.format(selectedDate.time))
            } else {
                Toast.makeText(this, "A data deve ser hoje ou maior.", Toast.LENGTH_SHORT).show()
            }
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun saveTask() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val deadline = binding.etDeadline.text.toString().trim()
        val details = binding.etDetails.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha Título, Descrição e Data.", Toast.LENGTH_SHORT).show()
            return
        }

        val taskToSave = selectedTask ?: Task() // Reutiliza a tarefa existente ou cria uma nova

        taskToSave.title = title
        taskToSave.description = description
        taskToSave.deadline = deadline
        taskToSave.details = details
        // isDeleted não é manipulado nesta tela

        val resultIntent = Intent().apply {
            putExtra(Constant.EXTRA_TASK, taskToSave)
            putExtra(Constant.EXTRA_IS_EDITING, isEditing) // Indica se é edição
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}