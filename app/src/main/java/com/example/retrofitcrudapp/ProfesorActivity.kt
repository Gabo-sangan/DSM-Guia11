package com.example.retrofitcrudapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfesorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var api: ProfesorApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewProfesores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        api = RetrofitClient.profesorInstance

        findViewById<FloatingActionButton>(R.id.fab_agregar_profesor).setOnClickListener {
            startActivity(Intent(this, CrearProfesorActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        api.obtenerProfesores().enqueue(object : Callback<List<Profesor>> {
            override fun onResponse(call: Call<List<Profesor>>, response: Response<List<Profesor>>) {
                if (response.isSuccessful) {
                    val profesores = response.body() ?: return
                    val adapter = ProfesorAdapter(profesores)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : ProfesorAdapter.OnItemClickListener {
                        override fun onItemClick(profesor: Profesor) {
                            val opciones = arrayOf("Modificar Profesor", "Eliminar Profesor")
                            AlertDialog.Builder(this@ProfesorActivity)
                                .setTitle(profesor.nombre)
                                .setItems(opciones) { _, index ->
                                    when (index) {
                                        0 -> modificar(profesor)
                                        1 -> eliminar(profesor)
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    })
                } else {
                    Log.e("API", "Error al obtener profesores: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProfesorActivity, "Error al obtener los profesores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Profesor>>, t: Throwable) {
                Log.e("API", "Error al obtener profesores: ${t.message}")
                Toast.makeText(this@ProfesorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun modificar(profesor: Profesor) {
        val i = Intent(this, ActualizarProfesorActivity::class.java)
        i.putExtra("profesor_id", profesor.id)
        i.putExtra("nombre", profesor.nombre)
        i.putExtra("apellido", profesor.apellido)
        i.putExtra("edad", profesor.edad)
        startActivity(i)
    }

    private fun eliminar(profesor: Profesor) {
        api.eliminarProfesor(profesor.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfesorActivity, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos()
                } else {
                    Log.e("API", "Error al eliminar: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProfesorActivity, "Error al eliminar profesor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar: $t")
                Toast.makeText(this@ProfesorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}