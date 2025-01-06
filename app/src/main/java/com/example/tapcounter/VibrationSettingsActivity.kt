package com.example.tapcounter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VibrationSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vibration_settings)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Titreşim Ayarları"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        window.statusBarColor = resources.getColor(android.R.color.black)

        setupViews()
    }

    private fun setupViews() {
        val switchVibration = findViewById<Switch>(R.id.switchVibration)
        val seekBarVibration = findViewById<SeekBar>(R.id.seekBarVibration)
        val textVibrationIntensity = findViewById<TextView>(R.id.textVibrationIntensity)

        // Titreşim özelliğini kontrol et
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        val hasVibrator = vibrator?.hasVibrator() == true

        if (!hasVibrator) {
            // Titreşim özelliği yoksa kontrolleri devre dışı bırak
            switchVibration.isEnabled = false
            seekBarVibration.isEnabled = false
            textVibrationIntensity.alpha = 0.5f
            switchVibration.text = "Titreşim (Bu cihazda kullanılamıyor)"
            return
        }

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        // Titreşim açık/kapalı
        switchVibration.isChecked = prefs.getBoolean("vibration_enabled", false)
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("vibration_enabled", isChecked).apply()
            seekBarVibration.isEnabled = isChecked
            textVibrationIntensity.alpha = if (isChecked) 1.0f else 0.5f
        }

        // Titreşim şiddeti
        val intensity = prefs.getInt("vibration_intensity", 50)
        seekBarVibration.progress = intensity
        seekBarVibration.isEnabled = switchVibration.isChecked
        textVibrationIntensity.text = "%$intensity"
        textVibrationIntensity.alpha = if (switchVibration.isChecked) 1.0f else 0.5f

        seekBarVibration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textVibrationIntensity.text = "%$progress"
                prefs.edit().putInt("vibration_intensity", progress).apply()
                
                // Test titreşimi
                if (fromUser) {
                    val duration = (progress * 0.9 + 10).toLong()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(duration)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
