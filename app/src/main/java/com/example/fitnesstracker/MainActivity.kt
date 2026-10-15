package com.example.fitnesstracker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var contador = 0
    private lateinit var sensorManager: SensorManager
    private lateinit var tvContador: TextView
    private var sensorPasos: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvContador = findViewById<TextView>(R.id.tvContador)
        val btnSumar = findViewById<Button>(R.id.btnSumar)
        val btnReiniciar = findViewById<Button>(R.id.btnReiniciar)
        val btnDetalle = findViewById<Button>(R.id.btnDetalle)

        val cardNatacion = findViewById<LinearLayout>(R.id.cardNatacion)
        val cardFuerza = findViewById<LinearLayout>(R.id.cardFuerza)
        val cardBox = findViewById<LinearLayout>(R.id.cardBox)

        val tvEstadoNatacion = findViewById<TextView>(R.id.tvEstadoNatacion)
        val tvEstadoFuerza = findViewById<TextView>(R.id.tvEstadoFuerza)
        val tvEstadoBox = findViewById<TextView>(R.id.tvEstadoBox)

        val checkNatacion = findViewById<CheckBox>(R.id.checkNatacion)
        val checkFuerza = findViewById<CheckBox>(R.id.checkFuerza)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 100)
        } else {
            configurarSensor()
        }

        btnSumar.setOnClickListener {
            contador++
            tvContador.text = "Numero de pasos: $contador"
        }

        btnReiniciar.setOnClickListener {
            contador = 0
            tvContador.text = "Numero de pasos: $contador"
        }

        btnDetalle.setOnClickListener {
            val intent = Intent(this, Detail::class.java)
            intent.putExtra("PASOS", contador)
            startActivity(intent)
        }

        tarjetaInt(cardNatacion, tvEstadoNatacion, checkNatacion)
        tarjetaInt(cardFuerza, tvEstadoFuerza, checkFuerza)
        tarjetaInt(cardBox, tvEstadoBox, checkBox)
    }

    private fun tarjetaInt(card: LinearLayout, tvEstado: TextView, checkBox: CheckBox) {
        val blanco = Color.parseColor("#FFFFFF")
        val verde = Color.parseColor("#C8E6C9")
        val gris = Color.parseColor("#888888")
        val textoVerde = Color.parseColor("#388E3C")

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                card.setBackgroundColor(verde)
                tvEstado.text = "Completado"
                tvEstado.setTextColor(textoVerde)
            } else {
                card.setBackgroundColor(blanco)
                tvEstado.text = "Pendiente"
                tvEstado.setTextColor(gris)
            }
        }

        card.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
        }
    }

    private fun configurarSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorPasos = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensorPasos == null) {
            tvContador.text = "Error al configurar el sensor de pasos"
        }
    }

    override fun onResume() {
        super.onResume()
        sensorPasos?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val pasosDetectados = event.values[0].toInt()
            contador = pasosDetectados
            tvContador.text = "Numero de pasos: $contador"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}