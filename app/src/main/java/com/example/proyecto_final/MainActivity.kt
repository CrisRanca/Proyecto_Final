package com.example.proyecto_final

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.HabitEntity
import com.example.proyecto_final.data.entities.SeguimientoEntity
import com.example.proyecto_final.utils.MascotaUtils
import com.example.proyecto_final.utils.TemaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : Activity() {

    private lateinit var contenedorHabitos: LinearLayout
    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvNivel: TextView
    private lateinit var tvPuntos: TextView
    private lateinit var imgAvatar: ImageView
    private lateinit var cabecera: LinearLayout
    private lateinit var btnMenu: TextView
    private lateinit var btnHoy: Button
    private lateinit var btnMisHabitos: Button
    private lateinit var btnAñadirHabito: Button

    private var idUsuario = -1
    private var colorTemaUsuario: String = "#E5E5E5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_habits)

        inicializarVistas()
        cargarPreferencias()
        aplicarTemaCabeceraYBotones()
        cargarUsuarioYCabecera(idUsuario)
        cargarHabitos(idUsuario)
        configurarBotones()

        // Botón HOY seleccionado por defecto
        seleccionarBoton(btnHoy)
    }

    override fun onResume() {
        super.onResume()
        cargarPreferencias()
        aplicarTemaCabeceraYBotones()
        cargarUsuarioYCabecera(idUsuario)
        cargarHabitos(idUsuario)
    }

    private fun inicializarVistas() {
        contenedorHabitos = findViewById(R.id.contenedorHabitos)
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvNivel = findViewById(R.id.tvNivel)
        tvPuntos = findViewById(R.id.tvPuntos)
        imgAvatar = findViewById(R.id.imgAvatar)
        cabecera = findViewById(R.id.cabeceraPrincipal)
        btnMenu = findViewById(R.id.btnMenu)
        btnHoy = findViewById(R.id.btnHoy)
        btnMisHabitos = findViewById(R.id.btnMisHabitos)
        btnAñadirHabito = findViewById(R.id.btnAñadirHabito)
    }

    private fun cargarPreferencias() {
        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        idUsuario = prefs.getInt("id_usuario_actual", -1)
        colorTemaUsuario = prefs.getString("color_tema_actual", "#E5E5E5") ?: "#E5E5E5"

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun aplicarTemaCabeceraYBotones() {
        cabecera.setBackgroundColor(Color.parseColor(colorTemaUsuario))

        TemaUtils.aplicarTemaBoton(this, btnHoy)
        TemaUtils.aplicarTemaBoton(this, btnMisHabitos)
        TemaUtils.aplicarTemaBoton(this, btnAñadirHabito)
    }

    private fun configurarBotones() {

        btnAñadirHabito.setOnClickListener {
            val intent = Intent(this, CrearHabitoActivity::class.java)
            intent.putExtra("ID_USUARIO", idUsuario)
            startActivityForResult(intent, 1001)
        }

        btnHoy.setOnClickListener {
            seleccionarBoton(btnHoy)
            cargarHabitos(idUsuario)
        }

        btnMisHabitos.setOnClickListener {
            seleccionarBoton(btnMisHabitos)
            cargarHabitos(idUsuario)
        }

        btnMenu.setOnClickListener {
            val popup = PopupMenu(this, btnMenu)
            popup.menu.add("Editar usuario")
            popup.menu.add("Cerrar sesión")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Editar usuario" -> startActivity(Intent(this, EditarUsuarioActivity::class.java))
                    "Cerrar sesión" -> {
                        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
                        prefs.edit().putBoolean("sesion_iniciada", false).apply()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
                true
            }

            popup.show()
        }
    }

    private fun seleccionarBoton(boton: Button) {

        val base = ContextCompat.getDrawable(this, R.drawable.button_outline)

        btnHoy.background = base
        btnMisHabitos.background = base

        TemaUtils.aplicarTemaBoton(this, btnHoy)
        TemaUtils.aplicarTemaBoton(this, btnMisHabitos)

        val borde = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 24f
            setStroke(2, Color.BLACK)
            setColor(Color.parseColor(colorTemaUsuario))
        }

        boton.background = borde
    }

    private fun cargarUsuarioYCabecera(idUsuario: Int) {
        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()

        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario)

            runOnUiThread {
                if (usuario != null) {
                    tvNombreUsuario.text = usuario.nombre
                    tvNivel.text = "Nivel: ${usuario.nivel}"
                    tvPuntos.text = "Puntos: ${usuario.puntosTotales}"

                    imgAvatar.setImageResource(
                        MascotaUtils.obtenerImagenMascota(usuario.nivel)
                    )
                }
            }
        }
    }

    private fun cargarHabitos(idUsuario: Int) {
        val db = HabiTrackDatabase.getInstance(this)
        val habitDao = db.habitDao()

        CoroutineScope(Dispatchers.IO).launch {
            val lista = habitDao.obtenerHabitosActivosPorUsuario(idUsuario)

            runOnUiThread {
                contenedorHabitos.removeAllViews()
                val inflater = LayoutInflater.from(this@MainActivity)

                for (habit in lista) {
                    val vista = inflater.inflate(R.layout.item_habit, contenedorHabitos, false)

                    val root = vista.findViewById<LinearLayout>(R.id.itemHabitRoot)
                    val tvTitulo = vista.findViewById<TextView>(R.id.tvTituloHabit)
                    val tvSubtitulo = vista.findViewById<TextView>(R.id.tvSubtituloHabit)
                    val cb = vista.findViewById<CheckBox>(R.id.cbCompletado)
                    val btnEditar = vista.findViewById<ImageButton>(R.id.btnEditar)

                    aplicarColorShape(root, habit.color)

                    tvTitulo.text = habit.titulo
                    tvSubtitulo.text = habit.subtitulo ?: ""

                    val hoy = normalizarFechaHoy()

                    CoroutineScope(Dispatchers.IO).launch {
                        val seguimientoDao = db.seguimientoDao()
                        val existente = seguimientoDao.obtenerPorHabitoYFecha(habit.idHabito, hoy)

                        runOnUiThread {
                            cb.isChecked = existente != null
                        }
                    }

                    root.setOnClickListener {
                        val intent = Intent(this@MainActivity, DetalleHabitoActivity::class.java)
                        intent.putExtra("ID_HABITO", habit.idHabito)
                        startActivity(intent)
                    }

                    btnEditar.setOnClickListener {
                        val intent = Intent(this@MainActivity, EditarHabitoActivity::class.java)
                        intent.putExtra("ID_HABITO", habit.idHabito)
                        startActivityForResult(intent, 1002)
                    }

                    cb.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) completarHabito(habit)
                        else descompletarHabito(habit)
                    }

                    contenedorHabitos.addView(vista)
                }
            }
        }
    }

    private fun aplicarColorShape(vista: android.view.View, colorHex: String) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 24f
            setColor(Color.parseColor(colorHex))
        }
        vista.background = drawable
    }

    private fun completarHabito(habit: HabitEntity) {
        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()
        val seguimientoDao = db.seguimientoDao()

        CoroutineScope(Dispatchers.IO).launch {

            val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario) ?: return@launch
            val hoy = normalizarFechaHoy()

            val existente = seguimientoDao.obtenerPorHabitoYFecha(habit.idHabito, hoy)

            if (existente == null) {
                val nuevo = SeguimientoEntity(
                    idSeguimiento = 0,
                    idHabito = habit.idHabito,
                    fecha = hoy,
                    nota = null,
                    animo = null,
                    vecesCumplimiento = 1
                )
                seguimientoDao.insertarSeguimiento(nuevo)
            }

            usuario.puntosTotales += 10
            usuario.nivel = usuario.puntosTotales / 100

            usuarioDao.actualizarUsuario(usuario)

            runOnUiThread {
                cargarUsuarioYCabecera(idUsuario)
            }
        }
    }

    private fun descompletarHabito(habit: HabitEntity) {
        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()
        val seguimientoDao = db.seguimientoDao()

        CoroutineScope(Dispatchers.IO).launch {

            val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario) ?: return@launch
            val hoy = normalizarFechaHoy()

            val existente = seguimientoDao.obtenerPorHabitoYFecha(habit.idHabito, hoy)

            if (existente != null) {
                seguimientoDao.borrarSeguimiento(existente.idSeguimiento)

                usuario.puntosTotales -= 10
                if (usuario.puntosTotales < 0) usuario.puntosTotales = 0

                usuario.nivel = usuario.puntosTotales / 100

                usuarioDao.actualizarUsuario(usuario)
            }

            runOnUiThread {
                cargarUsuarioYCabecera(idUsuario)
            }
        }
    }

    private fun normalizarFechaHoy(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        cargarPreferencias()
        aplicarTemaCabeceraYBotones()
        cargarUsuarioYCabecera(idUsuario)
        cargarHabitos(idUsuario)
    }
}
