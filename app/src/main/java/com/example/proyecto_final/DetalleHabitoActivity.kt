package com.example.proyecto_final

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.HabitEntity
import com.example.proyecto_final.data.entities.SeguimientoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DetalleHabitoActivity : Activity() {

    private var idHabito = -1

    private lateinit var tvTitulo: TextView
    private lateinit var tvSubtitulo: TextView
    private lateinit var tvFrecuencia: TextView
    private lateinit var tvNotificaciones: TextView
    private lateinit var tvAnimoPromedio: TextView
    private lateinit var tvTotalVeces: TextView
    private lateinit var tvPorcentaje: TextView
    private lateinit var gridCalendario: GridLayout
    private lateinit var btnRegistrar: Button
    private lateinit var cabecera: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_habito)

        idHabito = intent.getIntExtra("ID_HABITO", -1)
        if (idHabito == -1) {
            Toast.makeText(this, "Error: hábito no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        inicializarVistas()
        aplicarTemaCabecera()
        cargarDatos()
    }

    private fun inicializarVistas() {
        tvTitulo = findViewById(R.id.tvTituloDetalle)
        tvSubtitulo = findViewById(R.id.tvSubtituloDetalle)
        tvFrecuencia = findViewById(R.id.tvFrecuenciaDetalle)
        tvNotificaciones = findViewById(R.id.tvNotificacionesDetalle)
        tvAnimoPromedio = findViewById(R.id.tvAnimoPromedio)
        tvTotalVeces = findViewById(R.id.tvTotalVeces)
        tvPorcentaje = findViewById(R.id.tvPorcentaje)
        gridCalendario = findViewById(R.id.gridCalendario)
        btnRegistrar = findViewById(R.id.btnRegistrarSeguimiento)
        cabecera = findViewById(R.id.cabeceraDetalle)

        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegistrarSeguimientoActivity::class.java)
            intent.putExtra("ID_HABITO", idHabito)
            startActivity(intent)
        }
    }

    private fun aplicarTemaCabecera() {
        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        val color = prefs.getString("color_tema_actual", "#DDDDDD")!!
        cabecera.setBackgroundColor(Color.parseColor(color))
    }

    private fun cargarDatos() {
        val db = HabiTrackDatabase.getInstance(this)
        val habitDao = db.habitDao()
        val segDao = db.seguimientoDao()

        CoroutineScope(Dispatchers.IO).launch {
            val habito = habitDao.obtenerHabitoPorId(idHabito)
            val seguimientos = segDao.obtenerPorHabito(idHabito)

            runOnUiThread {
                if (habito != null) {
                    mostrarDatosHabito(habito)
                    mostrarEstadisticas(seguimientos, habito)
                    mostrarCalendario(seguimientos)
                }
            }
        }
    }

    private fun mostrarDatosHabito(h: HabitEntity) {
        tvTitulo.text = h.titulo
        tvSubtitulo.text = h.subtitulo ?: "Sin descripción"
        tvNotificaciones.text = if (h.notificaciones) "Activadas" else "Desactivadas"
        tvFrecuencia.text = interpretarFrecuencia(h.frecuencia)
    }

    private fun interpretarFrecuencia(json: String): String {
        return try {
            val obj = JSONObject(json)
            when (obj.getString("tipo")) {
                "diario" -> "Diariamente"
                "semanal" -> {
                    val dias = obj.getJSONArray("dias")
                    val nombres = listOf("L", "M", "X", "J", "V", "S", "D")
                    val lista = mutableListOf<String>()
                    for (i in 0 until dias.length()) {
                        lista.add(nombres[dias.getInt(i) - 1])
                    }
                    "Semanal: ${lista.joinToString(", ")}"
                }
                else -> "Personalizado"
            }
        } catch (e: Exception) {
            "Desconocida"
        }
    }

    private fun mostrarEstadisticas(lista: List<SeguimientoEntity>, habito: HabitEntity) {
        if (lista.isEmpty()) {
            tvAnimoPromedio.text = "—"
            tvTotalVeces.text = "0"
            tvPorcentaje.text = "0%"
            return
        }

        val totalVeces = lista.sumOf { it.vecesCumplimiento }
        val completados = lista.count { it.vecesCumplimiento >= habito.metaCumplimiento }

        val porcentaje = (completados.toDouble() / lista.size.toDouble()) * 100

        val animoPromedio = lista.mapNotNull { it.animo }.average().takeIf { !it.isNaN() }

        tvAnimoPromedio.text = animoPromedio?.let { String.format("%.1f", it) } ?: "—"
        tvTotalVeces.text = totalVeces.toString()
        tvPorcentaje.text = String.format("%.0f%%", porcentaje)
    }

    private fun mostrarCalendario(lista: List<SeguimientoEntity>) {
        gridCalendario.removeAllViews()

        val sdf = SimpleDateFormat("dd", Locale.getDefault())

        for (seg in lista) {
            val dia = sdf.format(Date(seg.fecha))

            val tv = TextView(this)
            tv.text = dia
            tv.textSize = 16f
            tv.setPadding(16, 16, 16, 16)
            tv.setBackgroundColor(if (seg.vecesCumplimiento > 0) Color.parseColor("#A5D6A7") else Color.parseColor("#EF9A9A"))
            tv.setTextColor(Color.BLACK)

            gridCalendario.addView(tv)
        }
    }
}
