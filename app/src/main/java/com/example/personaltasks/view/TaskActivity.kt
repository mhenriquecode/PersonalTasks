package com.example.personaltasks.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import com.example.personaltasks.databinding.ActivityTaskBinding
import com.example.personaltasks.model.Task
import java.util.Calendar

class TaskActivity : AppCompatActivity() {

    private val binding by lazy { ActivityTaskBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // DatePicker ao clicar no campo de data
        binding.etDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
                binding.etDeadline.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val deadline = binding.etDeadline.text.toString()

            val resultIntent = Intent().apply {
                putExtra("TASK_TITLE", title)
                putExtra("TASK_DESCRIPTION", description)
                putExtra("TASK_DEADLINE", deadline)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }

}