package com.example.proyecto_final

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.HabitEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditarHabitoActivity : Activity() {

    private lateinit var fondoCabecera: LinearLayout
    private lateinit var etTitulo: EditText
    private lateinit var etSubtitulo: EditText

    private lateinit var colorViews: List<LinearLayout>

    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private lateinit var rbDiario: RadioButton
    private lateinit var rbPersonalizado: RadioButton
    private lateinit var layoutDias: LinearLayout

    private var idHabito = -1
    private var colorSeleccionado = "#DDDDDD"
    private var ultimoSeleccionado: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_habito)

        inicializarVistas()
        configurarFrecuencia()

        idHabito = intent.getIntExtra("ID_HABITO", -1)
        cargarHabito()
        configurarSelectorColores()
        configurarBotones()
    }

    private fun inicializarVistas() {
        fondoCabecera = findViewById(R.id.fondoCabecera)
        etTitulo = findViewById(R.id.etTitulo)
        etSubtitulo = findViewById(R.id.etSubtitulo)

        colorViews = listOf(
            findViewById(R.id.colorGris),
            findViewById(R.id.colorNaranja),
            findViewById(R.id.colorVerde),
            findViewById(R.id.colorAzul),
            findViewById(R.id.colorMorado)
        )

        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        rbDiario = findViewById(R.id.rbDiario)
        rbPersonalizado = findViewById(R.id.rbPersonalizado)
        layoutDias = findViewById(R.id.layoutDiasPersonalizados)
    }

    private fun configurarFrecuencia() {
        rbDiario.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) layoutDias.visibility = View.GONE
        }

        rbPersonalizado.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) layoutDias.visibility = View.VISIBLE
        }
    }

    private fun cargarHabito() {
        val db = HabiTrackDatabase.getInstance(this)
        val habitDao = db.habitDao()

        CoroutineScope(Dispatchers.IO).launch {
            val habit = habitDao.obtenerHabitoPorId(idHabito)

            runOnUiThread {
                if (habit != null) {
                    etTitulo.setText(habit.titulo)
                    etSubtitulo.setText(habit.subtitulo ?: "")
                    colorSeleccionado = habit.color
                    aplicarColorHabito(colorSeleccionado)

                    // Marcar el color actual del hábito
                    val seleccionadoView = colorViews.firstOrDefault { it.tag == colorSeleccionado }
                    seleccionadoView?.let { aplicarBordeSeleccion(it) }
                }
            }
        }
    }

    private fun configurarSelectorColores() {
        colorViews.forEach { view ->
            view.setOnClickListener {
                val color = view.tag.toString()
                colorSeleccionado = color
                aplicarColorHabito(color)
                aplicarBordeSeleccion(view)
            }
        }
    }

    private fun aplicarBordeSeleccion(view: LinearLayout) {
        // Quitar borde anterior
        ultimoSeleccionado?.background = crearFondoColor(ultimoSeleccionado!!.tag.toString(), false)

        // Añadir borde al nuevo
        view.background = crearFondoColor(view.tag.toString(), true)

        ultimoSeleccionado = view
    }

    private fun crearFondoColor(color: String, seleccionado: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadius = 16f
            setColor(Color.parseColor(color))
            if (seleccionado) setStroke(4, Color.BLACK)
        }
    }

    private fun aplicarColorHabito(color: String) {
        val parsed = Color.parseColor(color)
        fondoCabecera.setBackgroundColor(parsed)
        btnGuardar.setBackgroundColor(parsed)
        btnCancelar.setBackgroundColor(parsed)
    }

    private fun configurarBotones() {
        btnGuardar.setOnClickListener { guardarCambios() }
        btnCancelar.setOnClickListener { finish() }
    }

    private fun guardarCambios() {
        val titulo = etTitulo.text.toString().trim()
        val subtitulo = etSubtitulo.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val db = HabiTrackDatabase.getInstance(this)
        val habitDao = db.habitDao()

        CoroutineScope(Dispatchers.IO).launch {
            val habit = habitDao.obtenerHabitoPorId(idHabito)

            if (habit != null) {
                val actualizado = HabitEntity(
                    idHabito = habit.idHabito,
                    idUsuario = habit.idUsuario,
                    titulo = titulo,
                    subtitulo = subtitulo,
                    color = colorSeleccionado,
                    activo = habit.activo
                )

                habitDao.actualizarHabito(actualizado)
            }

            runOnUiThread {
                Toast.makeText(this@EditarHabitoActivity, "Cambios guardados", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Extensión útil
    private fun <T> List<T>.firstOrDefault(predicate: (T) -> Boolean): T? {
        return this.firstOrNull(predicate)
    }
}
