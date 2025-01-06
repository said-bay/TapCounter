package com.example.tapcounter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        // Titreşim açık/kapalı
        switchVibration.isChecked = prefs.getBoolean("vibration_enabled", true)
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("vibration_enabled", isChecked).apply()
        }

        // Titreşim şiddeti
        val intensity = prefs.getInt("vibration_intensity", 50)
        seekBarVibration.progress = intensity
        textVibrationIntensity.text = "%$intensity"

        seekBarVibration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textVibrationIntensity.text = "%$progress"
                prefs.edit().putInt("vibration_intensity", progress).apply()
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
