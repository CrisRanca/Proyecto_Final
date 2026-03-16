package com.example.proyecto_final

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.HabitEntity
import com.example.proyecto_final.utils.TemaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrearHabitoActivity : Activity() {

    private lateinit var etTitulo: EditText
    private lateinit var etSubtitulo: EditText
    private lateinit var switchRecordatorio: Switch
    private lateinit var rgFrecuencia: RadioGroup
    private lateinit var layoutDiasPersonalizados: LinearLayout

    // CheckBoxes de días
    private lateinit var chkLunes: CheckBox
    private lateinit var chkMartes: CheckBox
    private lateinit var chkMiercoles: CheckBox
    private lateinit var chkJueves: CheckBox
    private lateinit var chkViernes: CheckBox
    private lateinit var chkSabado: CheckBox
    private lateinit var chkDomingo: CheckBox

    // Vistas de color
    private lateinit var viewColorGris: LinearLayout
    private lateinit var viewColorNaranja: LinearLayout
    private lateinit var viewColorVerde: LinearLayout
    private lateinit var viewColorAzul: LinearLayout
    private lateinit var viewColorMorado: LinearLayout

    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var cabecera: LinearLayout

    private var colorSeleccionado = "#DDDDDD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_habito)

        inicializarVistas()
        aplicarTemaCabecera()
        aplicarTemaBotones()
        configurarColores()
        configurarFrecuencia()
        configurarBotones()
    }

    private fun inicializarVistas() {
        etTitulo = findViewById(R.id.etTitulo)
        etSubtitulo = findViewById(R.id.etSubtitulo)
        switchRecordatorio = findViewById(R.id.switchRecordatorio)
        rgFrecuencia = findViewById(R.id.rgFrecuencia)
        layoutDiasPersonalizados = findViewById(R.id.layoutDiasPersonalizados)

        chkLunes = findViewById(R.id.chkLunes)
        chkMartes = findViewById(R.id.chkMartes)
        chkMiercoles = findViewById(R.id.chkMiercoles)
        chkJueves = findViewById(R.id.chkJueves)
        chkViernes = findViewById(R.id.chkViernes)
        chkSabado = findViewById(R.id.chkSabado)
        chkDomingo = findViewById(R.id.chkDomingo)

        viewColorGris = findViewById(R.id.colorGris)
        viewColorNaranja = findViewById(R.id.colorNaranja)
        viewColorVerde = findViewById(R.id.colorVerde)
        viewColorAzul = findViewById(R.id.colorAzul)
        viewColorMorado = findViewById(R.id.colorMorado)

        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        cabecera = findViewById(R.id.fondoCabecera)
    }

    private fun aplicarTemaCabecera() {
        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        val colorHex = prefs.getString("color_tema_actual", "#DDDDDD")!!
        cabecera.setBackgroundColor(Color.parseColor(colorHex))
    }

    private fun aplicarTemaBotones() {
        TemaUtils.aplicarTemaBoton(this, btnGuardar)
        TemaUtils.aplicarTemaBoton(this, btnCancelar)
    }

    private fun configurarColores() {
        colorSeleccionado = "#DDDDDD"
        marcarColorSeleccionado(viewColorGris)

        viewColorGris.setOnClickListener { seleccionarColor("#DDDDDD", viewColorGris) }
        viewColorNaranja.setOnClickListener { seleccionarColor("#FAE5D3", viewColorNaranja) }
        viewColorVerde.setOnClickListener { seleccionarColor("#D4EFDF", viewColorVerde) }
        viewColorAzul.setOnClickListener { seleccionarColor("#D1E8FF", viewColorAzul) }
        viewColorMorado.setOnClickListener { seleccionarColor("#EBDEF0", viewColorMorado) }
    }

    private fun seleccionarColor(color: String, vista: LinearLayout) {
        colorSeleccionado = color
        marcarColorSeleccionado(vista)
    }

    private fun marcarColorSeleccionado(seleccionado: LinearLayout) {
        limpiarSeleccionColores()
        seleccionado.foreground = ContextCompat.getDrawable(this, R.drawable.color_selected)
    }

    private fun limpiarSeleccionColores() {
        viewColorGris.foreground = null
        viewColorNaranja.foreground = null
        viewColorVerde.foreground = null
        viewColorAzul.foreground = null
        viewColorMorado.foreground = null
    }

    private fun configurarFrecuencia() {
        rgFrecuencia.setOnCheckedChangeListener { _, checkedId ->
            layoutDiasPersonalizados.visibility =
                if (checkedId == R.id.rbPersonalizado) View.VISIBLE else View.GONE
        }
    }

    private fun configurarBotones() {
        btnGuardar.setOnClickListener { guardarHabito() }
        btnCancelar.setOnClickListener { finish() }
    }

    private fun construirFrecuenciaJson(): String {
        return if (rgFrecuencia.checkedRadioButtonId == R.id.rbDiario) {
            """{"tipo":"diario"}"""
        } else {
            val dias = mutableListOf<Int>()
            if (chkLunes.isChecked) dias.add(1)
            if (chkMartes.isChecked) dias.add(2)
            if (chkMiercoles.isChecked) dias.add(3)
            if (chkJueves.isChecked) dias.add(4)
            if (chkViernes.isChecked) dias.add(5)
            if (chkSabado.isChecked) dias.add(6)
            if (chkDomingo.isChecked) dias.add(7)

            if (dias.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un día", Toast.LENGTH_SHORT).show()
                return """{"tipo":"diario"}"""
            }

            """{"tipo":"semanal","dias":[${dias.joinToString(",")}]}"""
        }
    }

    private fun guardarHabito() {
        val titulo = etTitulo.text.toString().trim()
        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario_actual", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoHabito = HabitEntity(
            idUsuario = idUsuario,
            titulo = titulo,
            subtitulo = etSubtitulo.text.toString().trim().ifEmpty { null },
            color = colorSeleccionado,
            frecuencia = construirFrecuenciaJson(),
            notificaciones = switchRecordatorio.isChecked,
            horaRecordatorio = if (switchRecordatorio.isChecked) "08:00" else null,
            tipoCumplimiento = "boolean",
            metaCumplimiento = 1
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = HabiTrackDatabase.getInstance(this@CrearHabitoActivity)
            db.habitDao().insertarHabito(nuevoHabito)

            runOnUiThread {
                Toast.makeText(this@CrearHabitoActivity, "¡Hábito creado!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}
