package com.example.personaltasks.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltasks.R
import com.example.personaltasks.adapter.TaskAdapter
import com.example.personaltasks.databinding.ActivityMainBinding
import com.example.personaltasks.model.Task

class MainActivity : AppCompatActivity() {
    // ViewBinding
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(amb.root)

        // lista de fake tasks
        val taskList = listOf(
            Task("Estudar tests", "Aprender testes de mutação", "21/05/2025"),
            Task("Ir para academia", "Fazer treino de peito e ombro", "22/05/2025"),
            Task("Entregar tarefa", "tarefa resumo de redes I", "23/05/2025"),
            Task("Estudar tests", "Aprender testes de mutação", "21/05/2025"),
            Task("Ir para academia", "Fazer treino de peito e ombro", "22/05/2025"),
            Task("Entregar tarefa", "tarefa resumo de redes I", "23/05/2025"),
            Task("Estudar tests", "Aprender testes de mutação", "21/05/2025"),
            Task("Ir para academia", "Fazer treino de peito e ombro", "22/05/2025"),
            Task("Entregar tarefa", "tarefa resumo de redes I", "23/05/2025")
        )

        // RecyclerView usando o binding
        amb.rvTaks.layoutManager = LinearLayoutManager(this)
        amb.rvTaks.adapter = TaskAdapter(taskList)
    }
}