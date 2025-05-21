package com.example.personaltasks.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.view.View
import com.example.personaltasks.databinding.ActivityTaskBinding
import com.example.personaltasks.model.Constant.EXTRA_TASK
import com.example.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.example.personaltasks.model.Task
import java.util.Calendar

class TaskActivity : AppCompatActivity() {

    private val binding: ActivityTaskBinding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra<Task>(EXTRA_TASK)
        }

        receivedTask?.let { task ->
            with(binding) {
                etTitle.setText(task.title)
                etDescription.setText(task.description)
                etDeadline.setText(task.deadline)
                etDetails.setText(task.details)
                val isViewOnly = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
                if (isViewOnly) {
                    etTitle.isEnabled = false
                    etDescription.isEnabled = false
                    etDeadline.isEnabled = false
                    etDetails.isEnabled = false
                    btnSave.visibility = View.GONE
                }
            }
        }
        // data
        with(binding) {
            etDeadline.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(this@TaskActivity, { _, y, m, d ->
                    etDeadline.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
                }, year, month, day).show()
            }
        }
        // botão salvar
        with(binding) {
            btnSave.setOnClickListener {
                val task = Task(
                    receivedTask?.id ?: hashCode(),
                    etTitle.text.toString(),
                    etDescription.text.toString(),
                    etDeadline.text.toString(),
                    etDetails.text.toString()
                )
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_TASK, task)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        // botão cancelar
        binding.btnCancel.setOnClickListener{
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}