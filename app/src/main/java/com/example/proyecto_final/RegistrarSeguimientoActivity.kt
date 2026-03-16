package com.example.proyecto_final

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.SeguimientoEntity
import com.example.proyecto_final.utils.FechaUtils
import com.example.proyecto_final.utils.RachaUtils
import com.example.proyecto_final.utils.TemaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrarSeguimientoActivity : Activity() {

    private var idHabito = -1

    private lateinit var tvTitulo: TextView
    private lateinit var etNota: EditText
    private lateinit var etVeces: EditText
    private lateinit var rgAnimo: RadioGroup
    private lateinit var btnGuardar: Button
    private lateinit var cabecera: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_seguimiento)

        idHabito = intent.getIntExtra("ID_HABITO", -1)
        if (idHabito == -1) {
            Toast.makeText(this, "Error: hábito no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        inicializarVistas()
        aplicarTemaCabecera()
        cargarTitulo()
    }

    private fun inicializarVistas() {
        tvTitulo = findViewById(R.id.tvTituloRegistrar)
        etNota = findViewById(R.id.etNotaSeguimiento)
        etVeces = findViewById(R.id.etVecesCumplimiento)
        rgAnimo = findViewById(R.id.rgAnimo)
        btnGuardar = findViewById(R.id.btnGuardarSeguimiento)
        cabecera = findViewById(R.id.cabeceraRegistrar)

        TemaUtils.aplicarTemaBoton(this, btnGuardar)

        btnGuardar.setOnClickListener { guardarSeguimiento() }
    }

    private fun aplicarTemaCabecera() {
        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        val color = prefs.getString("color_tema_actual", "#DDDDDD")!!
        cabecera.setBackgroundColor(android.graphics.Color.parseColor(color))
    }

    private fun cargarTitulo() {
        val db = HabiTrackDatabase.getInstance(this)
        val habitDao = db.habitDao()

        CoroutineScope(Dispatchers.IO).launch {
            val habito = habitDao.obtenerHabitoPorId(idHabito)
            runOnUiThread {
                tvTitulo.text = habito?.titulo ?: "Registrar seguimiento"
            }
        }
    }

    private fun guardarSeguimiento() {
        val nota = etNota.text.toString().trim().ifEmpty { null }
        val veces = etVeces.text.toString().toIntOrNull() ?: 0

        val animo = when (rgAnimo.checkedRadioButtonId) {
            R.id.rb1 -> 1
            R.id.rb2 -> 2
            R.id.rb3 -> 3
            R.id.rb4 -> 4
            R.id.rb5 -> 5
            else -> null
        }

        val fechaHoy = FechaUtils.hoyMillis()

        val db = HabiTrackDatabase.getInstance(this)
        val segDao = db.seguimientoDao()
        val habitDao = db.habitDao()
        val usuarioDao = db.usuarioDao()

        CoroutineScope(Dispatchers.IO).launch {

            // 1. Comprobar si ya existe seguimiento hoy
            val existente = segDao.obtenerPorHabitoYFecha(idHabito, fechaHoy)
            if (existente != null) {
                runOnUiThread {
                    Toast.makeText(
                        this@RegistrarSeguimientoActivity,
                        "Ya existe un seguimiento hoy",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            // 2. Insertar seguimiento
            val nuevo = SeguimientoEntity(
                idHabito = idHabito,
                fecha = fechaHoy,
                nota = nota,
                animo = animo,
                vecesCumplimiento = veces
            )
            segDao.insertarSeguimiento(nuevo)

            // 3. Recalcular racha + puntos + nivel
            RachaUtils.recalcularRachaYPuntos(
                idHabito = idHabito,
                db = db
            )

            runOnUiThread {
                Toast.makeText(
                    this@RegistrarSeguimientoActivity,
                    "Seguimiento registrado",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
