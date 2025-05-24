package com.example.personaltasks.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.view.View
import com.example.personaltasks.databinding.ActivityTaskBinding
import com.example.personaltasks.model.Constant.EXTRA_TASK
import com.example.personaltasks.model.Constant.EXTRA_TASK_POSITION
import com.example.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.personaltasks.model.Task
import java.util.Calendar

class TaskActivity : AppCompatActivity() {

    // Faz o vínculo com a interface gráfica (ViewBinding)
    private val binding: ActivityTaskBinding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    @SuppressLint("SimpleDateFormat", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // Define a visualização da tela

        // Recupera a tarefa enviada pela MainActivity (se houver)
        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra<Task>(EXTRA_TASK)
        }

        // Se recebeu uma tarefa, preenche os campos com os dados dela
        receivedTask?.let { task ->
            with(binding) {
                etTitle.setText(task.title)
                etDescription.setText(task.description)
                etDeadline.setText(task.deadline)
                etDetails.setText(task.details)

                // Verifica se a tarefa está sendo visualizada apenas (sem edição)
                val isViewOnly = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
                if (isViewOnly) {
                    etTitle.isEnabled = false
                    etDescription.isEnabled = false
                    etDeadline.isEnabled = false
                    etDetails.isEnabled = false
                    btnSave.visibility = View.GONE // Esconde o botão salvar
                }
            }
        }

        // Quando o usuário clica no campo de data, abre o DatePicker
        with(binding) {
            etDeadline.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                // Mostra o DatePicker com a data atual como padrão
                DatePickerDialog(this@TaskActivity, { _, y, m, d ->
                    etDeadline.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
                }, year, month, day).show()
            }
        }

        // Quando o botão salvar é clicado
        with(binding) {
            btnSave.setOnClickListener {
                // Lê e limpa os campos de entrada
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val deadline = etDeadline.text.toString().trim()
                val details = etDetails.text.toString().trim()

                // Verifica se o título está em branco
                if (title.isBlank()) {
                    etTitle.error = "O título não pode estar em branco"
                    etTitle.requestFocus()
                    return@setOnClickListener
                }

                // Validação da data usando SimpleDateFormat
                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy")
                sdf.isLenient = false

                try {
                    val taskDate = sdf.parse(deadline)
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time

                    // Verifica se a data informada é anterior à data atual
                    if (taskDate.before(today)) {
                        etDeadline.error = "A data não pode ser anterior à hoje"
                        etDeadline.requestFocus()
                        return@setOnClickListener
                    }
                } catch (e: Exception) {
                    etDeadline.error = "Data inválida"
                    etDeadline.requestFocus()
                    return@setOnClickListener
                }

                // Cria uma nova tarefa com os dados preenchidos
                val task = Task(
                    receivedTask?.id ?: hashCode(), // Usa o ID anterior ou gera um novo
                    title,
                    description,
                    deadline,
                    details
                )

                // Recupera a posição da tarefa (caso esteja editando)
                val position = intent.getIntExtra(EXTRA_TASK_POSITION, -1)

                // Cria um intent com a tarefa e a posição para devolver à MainActivity
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_TASK, task)
                    putExtra(EXTRA_TASK_POSITION, position)
                }

                setResult(Activity.RESULT_OK, resultIntent) // Define o resultado
                finish() // Fecha a tela
            }
        }

        // Quando o botão cancelar é clicado
        with(binding){
            btnCancel.setOnClickListener {
                setResult(Activity.RESULT_CANCELED) // Informa que foi cancelado
                finish()
            }
        }
    }
}
