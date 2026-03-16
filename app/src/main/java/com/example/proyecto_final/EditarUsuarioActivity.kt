package com.example.proyecto_final

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.UsuarioEntity
import com.example.proyecto_final.utils.MascotaUtils
import com.example.proyecto_final.utils.TemaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditarUsuarioActivity : Activity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText

    private lateinit var imgMascota1: ImageView
    private lateinit var imgMascota2: ImageView
    private lateinit var imgMascota3: ImageView
    private lateinit var imgMascota4: ImageView
    private lateinit var imgMascota5: ImageView

    private lateinit var colorGris: LinearLayout
    private lateinit var colorNaranja: LinearLayout
    private lateinit var colorVerde: LinearLayout
    private lateinit var colorAzul: LinearLayout
    private lateinit var colorMorado: LinearLayout

    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var cabecera: LinearLayout

    private var mascotaSeleccionada: String = "lv1"
    private var colorTemaSeleccionado: String = "#E5E5E5"
    private var idUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        inicializarVistas()
        cargarPreferencias()
        aplicarTemaCabecera()
        aplicarTemaBotones()
        configurarMascotas()
        configurarColores()
        cargarDatosUsuario()
        configurarBotones()
    }

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.etNombreUsuario)
        etEmail = findViewById(R.id.etEmailUsuario)

        imgMascota1 = findViewById(R.id.imgMascota1)
        imgMascota2 = findViewById(R.id.imgMascota2)
        imgMascota3 = findViewById(R.id.imgMascota3)
        imgMascota4 = findViewById(R.id.imgMascota4)
        imgMascota5 = findViewById(R.id.imgMascota5)

        colorGris = findViewById(R.id.colorGris)
        colorNaranja = findViewById(R.id.colorNaranja)
        colorVerde = findViewById(R.id.colorVerde)
        colorAzul = findViewById(R.id.colorAzul)
        colorMorado = findViewById(R.id.colorMorado)

        btnGuardar = findViewById(R.id.btnGuardarUsuario)
        btnCancelar = findViewById(R.id.btnCancelarUsuario)
        cabecera = findViewById(R.id.cabeceraEditarUsuario)
    }

    private fun cargarPreferencias() {
        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        idUsuario = prefs.getInt("id_usuario_actual", -1)
        colorTemaSeleccionado = prefs.getString("color_tema_actual", "#E5E5E5")!!
    }

    private fun aplicarTemaCabecera() {
        cabecera.setBackgroundColor(Color.parseColor(colorTemaSeleccionado))
    }

    private fun aplicarTemaBotones() {
        TemaUtils.aplicarTemaBoton(this, btnGuardar)
        TemaUtils.aplicarTemaBoton(this, btnCancelar)
    }

    private fun configurarMascotas() {
        imgMascota1.setOnClickListener { seleccionarMascota("lv1", imgMascota1) }
        imgMascota2.setOnClickListener { seleccionarMascota("lv5", imgMascota2) }
        imgMascota3.setOnClickListener { seleccionarMascota("lv10", imgMascota3) }
        imgMascota4.setOnClickListener { seleccionarMascota("lv20", imgMascota4) }
        imgMascota5.setOnClickListener { seleccionarMascota("lv30", imgMascota5) }
    }

    private fun seleccionarMascota(nombre: String, vista: ImageView) {
        mascotaSeleccionada = nombre
        limpiarSeleccionMascotas()
        vista.foreground = ContextCompat.getDrawable(this, R.drawable.color_selected)
    }

    private fun limpiarSeleccionMascotas() {
        imgMascota1.foreground = null
        imgMascota2.foreground = null
        imgMascota3.foreground = null
        imgMascota4.foreground = null
        imgMascota5.foreground = null
    }

    private fun configurarColores() {
        colorGris.setOnClickListener { seleccionarColor("#E5E5E5", colorGris) }
        colorNaranja.setOnClickListener { seleccionarColor("#FAE5D3", colorNaranja) }
        colorVerde.setOnClickListener { seleccionarColor("#D4EFDF", colorVerde) }
        colorAzul.setOnClickListener { seleccionarColor("#D1E8FF", colorAzul) }
        colorMorado.setOnClickListener { seleccionarColor("#EBDEF0", colorMorado) }
    }

    private fun seleccionarColor(color: String, vista: LinearLayout) {
        colorTemaSeleccionado = color
        limpiarSeleccionColores()
        vista.foreground = ContextCompat.getDrawable(this, R.drawable.color_selected)
    }

    private fun limpiarSeleccionColores() {
        colorGris.foreground = null
        colorNaranja.foreground = null
        colorVerde.foreground = null
        colorAzul.foreground = null
        colorMorado.foreground = null
    }

    private fun cargarDatosUsuario() {
        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()

        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario)

            runOnUiThread {
                if (usuario != null) {
                    etNombre.setText(usuario.nombre)
                    etEmail.setText(usuario.email)

                    mascotaSeleccionada = usuario.mascota ?: "lv1"
                    marcarMascotaActual(mascotaSeleccionada)

                    colorTemaSeleccionado = usuario.colorTema
                    marcarColorActual(colorTemaSeleccionado)
                }
            }
        }
    }

    private fun marcarMascotaActual(nombre: String) {
        when (nombre) {
            "lv1" -> seleccionarMascota("lv1", imgMascota1)
            "lv5" -> seleccionarMascota("lv5", imgMascota2)
            "lv10" -> seleccionarMascota("lv10", imgMascota3)
            "lv20" -> seleccionarMascota("lv20", imgMascota4)
            "lv30" -> seleccionarMascota("lv30", imgMascota5)
        }
    }

    private fun marcarColorActual(color: String) {
        when (color) {
            "#E5E5E5" -> seleccionarColor("#E5E5E5", colorGris)
            "#FAE5D3" -> seleccionarColor("#FAE5D3", colorNaranja)
            "#D4EFDF" -> seleccionarColor("#D4EFDF", colorVerde)
            "#D1E8FF" -> seleccionarColor("#D1E8FF", colorAzul)
            "#EBDEF0" -> seleccionarColor("#EBDEF0", colorMorado)
        }
    }

    private fun configurarBotones() {
        btnGuardar.setOnClickListener { guardarCambios() }
        btnCancelar.setOnClickListener { finish() }
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "El email no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()

        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario)

            if (usuario != null) {
                val actualizado = usuario.copy(
                    nombre = nombre,
                    email = email,
                    mascota = mascotaSeleccionada,
                    colorTema = colorTemaSeleccionado
                )

                usuarioDao.actualizarUsuario(actualizado)

                val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("color_tema_actual", colorTemaSeleccionado)
                    .apply()

                runOnUiThread {
                    Toast.makeText(this@EditarUsuarioActivity, "Cambios guardados", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
