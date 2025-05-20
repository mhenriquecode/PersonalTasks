package com.example.personaltasks.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
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
        setSupportActionBar(amb.toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_task_menu -> {
                val intent = Intent(this, TaskActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

}