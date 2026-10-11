package com.example.retrofitcrudapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearProfesorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_profesor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombreEditText: EditText = findViewById(R.id.editTextNombreProf)
        val apellidoEditText: EditText = findViewById(R.id.editTextApellidoProf)
        val edadEditText: EditText = findViewById(R.id.editTextEdadProf)
        val guardarButton: Button = findViewById(R.id.btnGuardarProf)

        guardarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val apellido = apellidoEditText.text.toString().trim()
            val edad = edadEditText.text.toString().trim().toIntOrNull()

            Log.d("API", "Click guardar -> nombre='$nombre' apellido='$apellido' edad=$edad")

            if (nombre.isEmpty() || apellido.isEmpty() || edad == null) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val profesor = Profesor(0, nombre, apellido, edad)
            Log.d("API", "Enviando profesor: $profesor")
            RetrofitClient.profesorInstance.crearProfesor(profesor)
                .enqueue(object : Callback<Profesor> {
                    override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CrearProfesorActivity, "Profesor creado exitosamente", Toast.LENGTH_SHORT).show()
                            finish() // vuelve a la lista, que se recarga en onResume
                        } else {
                            Log.e("API", "Error crear profesor: ${response.errorBody()?.string()}")
                            Toast.makeText(this@CrearProfesorActivity, "Error al crear el profesor", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Profesor>, t: Throwable) {
                        Log.e("API", "onFailure: $t")
                        Toast.makeText(this@CrearProfesorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}