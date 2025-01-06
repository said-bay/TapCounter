package com.example.tapcounter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import android.text.InputType
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Toolbar'ı ayarla
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Ayarlar"
        }

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnVibrationSettings).setOnClickListener {
            startActivity(Intent(this, VibrationSettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnCounterSettings).setOnClickListener {
            startActivity(Intent(this, CounterSettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnSaved).setOnClickListener {
            startActivity(Intent(this, SavedCountersActivity::class.java))
        }

        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun showCounterSettingsDialog() {
        val settingsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
        }

        // Sayaç Konumu
        val positionText = TextView(this).apply {
            text = "Sayaç Konumu"
            setTextColor(resources.getColor(android.R.color.white, theme))
            textSize = 16f
        }
        settingsLayout.addView(positionText)

        val positions = arrayOf("Orta", "Sol Üst", "Sağ Üst", "Sol Alt", "Sağ Alt")
        val currentPosition = getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getInt("counter_position", 0)

        val positionSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(context, R.layout.spinner_item, positions).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            setSelection(currentPosition)
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
            setPopupBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        }
        settingsLayout.addView(positionSpinner)

        // Sayaç Rengi
        val colorText = TextView(this).apply {
            text = "Sayaç Rengi"
            setTextColor(resources.getColor(android.R.color.white, theme))
            textSize = 16f
            setPadding(0, 30, 0, 0)
        }
        settingsLayout.addView(colorText)

        val colors = arrayOf("Beyaz", "Kırmızı", "Yeşil", "Mavi", "Sarı")
        val currentColor = getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getInt("counter_color", 0)

        val colorSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(context, R.layout.spinner_item, colors).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            setSelection(currentColor)
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
            setPopupBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        }
        settingsLayout.addView(colorSpinner)

        // Sayaç Boyutu
        val sizeText = TextView(this).apply {
            text = "Sayaç Boyutu"
            setTextColor(resources.getColor(android.R.color.white, theme))
            textSize = 16f
            setPadding(0, 30, 0, 0)
        }
        settingsLayout.addView(sizeText)

        val sizes = arrayOf("Küçük", "Normal", "Büyük", "Çok Büyük")
        val currentSize = getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getInt("counter_size", 1)

        val sizeSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(context, R.layout.spinner_item, sizes).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            setSelection(currentSize)
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
            setPopupBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        }
        settingsLayout.addView(sizeSpinner)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Sayaç Ayarları")
            .setView(settingsLayout)
            .setPositiveButton("Kaydet") { _, _ ->
                val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putInt("counter_position", positionSpinner.selectedItemPosition)
                    putInt("counter_color", colorSpinner.selectedItemPosition)
                    putInt("counter_size", sizeSpinner.selectedItemPosition)
                }.apply()
            }
            .setNegativeButton("İptal", null)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        dialog.show()
    }

    private fun showAboutDialog() {
        val messageView = TextView(this).apply {
            text = "Sayaç Uygulaması v1.0\n\nGeliştirici: Ahmet"
            setTextColor(resources.getColor(android.R.color.white, theme))
            textSize = 16f
            setPadding(50, 30, 50, 30)
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Hakkında")
            .setView(messageView)
            .setPositiveButton("Tamam", null)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
