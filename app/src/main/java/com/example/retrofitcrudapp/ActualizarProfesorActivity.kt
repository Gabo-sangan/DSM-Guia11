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

class ActualizarProfesorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actualizar_profesor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombreEditText: EditText = findViewById(R.id.nombreProfEditText)
        val apellidoEditText: EditText = findViewById(R.id.apellidoProfEditText)
        val edadEditText: EditText = findViewById(R.id.edadProfEditText)
        val actualizarButton: Button = findViewById(R.id.actualizarProfButton)

        val profesorId = intent.getIntExtra("profesor_id", -1)
        nombreEditText.setText(intent.getStringExtra("nombre"))
        apellidoEditText.setText(intent.getStringExtra("apellido"))
        edadEditText.setText(intent.getIntExtra("edad", 0).toString())

        actualizarButton.setOnClickListener {
            val edad = edadEditText.text.toString().trim().toIntOrNull()
            if (edad == null) {
                Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val actualizado = Profesor(
                profesorId,
                nombreEditText.text.toString().trim(),
                apellidoEditText.text.toString().trim(),
                edad
            )

            RetrofitClient.profesorInstance.actualizarProfesor(profesorId, actualizado)
                .enqueue(object : Callback<Profesor> {
                    override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ActualizarProfesorActivity, "Profesor actualizado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.e("API", "Error actualizar: ${response.errorBody()?.string()}")
                            Toast.makeText(this@ActualizarProfesorActivity, "Error al actualizar el profesor", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Profesor>, t: Throwable) {
                        Log.e("API", "onFailure: $t")
                        Toast.makeText(this@ActualizarProfesorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}