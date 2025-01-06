package com.example.tapcounter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class CounterSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter_settings)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sayaç Ayarları"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        window.statusBarColor = resources.getColor(android.R.color.black)

        setupSpinners()
    }

    private fun setupSpinners() {
        val positions = arrayOf("Orta", "Sol Üst", "Sağ Üst", "Sol Alt", "Sağ Alt")
        val colors = arrayOf("Beyaz", "Kırmızı", "Yeşil", "Mavi", "Sarı")
        val sizes = arrayOf("Küçük", "Normal", "Büyük", "Çok Büyük")

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        findViewById<Spinner>(R.id.positionSpinner).apply {
            adapter = ArrayAdapter(context, R.layout.spinner_item, positions).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            setSelection(prefs.getInt("counter_position", 0))
            setPopupBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        }

        findViewById<Spinner>(R.id.colorSpinner).apply {
            adapter = ArrayAdapter(context, R.layout.spinner_item, colors).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            setSelection(prefs.getInt("counter_color", 0))
            setPopupBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        }

        findViewById<Spinner>(R.id.sizeSpinner).apply {
            adapter = ArrayAdapter(context, R.layout.spinner_item, sizes).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            setSelection(prefs.getInt("counter_size", 1))
            setPopupBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("counter_position", findViewById<Spinner>(R.id.positionSpinner).selectedItemPosition)
            putInt("counter_color", findViewById<Spinner>(R.id.colorSpinner).selectedItemPosition)
            putInt("counter_size", findViewById<Spinner>(R.id.sizeSpinner).selectedItemPosition)
        }.apply()
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
