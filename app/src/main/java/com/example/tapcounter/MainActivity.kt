package com.example.tapcounter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    private lateinit var counterText: TextView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var prefs: SharedPreferences
    private var vibrator: Vibrator? = null
    private var counter = 0

    companion object {
        private const val PREF_COUNTER = "counter"
        private const val PREF_FONT_SIZE = "font_size"
        private const val PREF_FONT_COLOR = "font_color"
        private const val PREF_COUNTER_POSITION = "counter_position"
        private const val PREF_VIBRATION_ENABLED = "vibration_enabled"
        private const val PREF_VIBRATION_STRENGTH = "vibration_strength"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getPreferences(Context.MODE_PRIVATE)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        
        setContentView(R.layout.activity_main)
        
        // Toolbar'ı ayarla
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        counterText = findViewById(R.id.counterText)
        mainLayout = findViewById(R.id.mainLayout)
        
        // Kaydedilmiş sayacı yükle
        counter = savedInstanceState?.getInt("counter", 0) ?: prefs.getInt(PREF_COUNTER, 0)
        updateCounter()
        
        // Ayarları uygula
        applySettings()

        // Ekrana tıklama işlemi
        mainLayout.setOnClickListener {
            counter++
            updateCounter()
            performVibration()
        }
    }

    private fun applySettings() {
        // Yazı boyutunu ayarla
        val fontSize = when (prefs.getString(PREF_FONT_SIZE, "medium")) {
            "small" -> 24f
            "medium" -> 36f
            "large" -> 48f
            else -> 36f
        }
        counterText.textSize = fontSize

        // Yazı rengini ayarla
        counterText.setTextColor(prefs.getInt(PREF_FONT_COLOR, Color.WHITE))

        // Konumu ayarla
        val params = counterText.layoutParams as ConstraintLayout.LayoutParams
        when (prefs.getString(PREF_COUNTER_POSITION, "center")) {
            "top" -> {
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                params.verticalBias = 0.1f
            }
            "center" -> {
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.verticalBias = 0.5f
            }
            "bottom" -> {
                params.topToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.verticalBias = 0.9f
            }
        }
        counterText.layoutParams = params

        // Arka plan rengini siyah yap
        window.decorView.setBackgroundColor(Color.BLACK)
        findViewById<View>(android.R.id.content).setBackgroundColor(Color.BLACK)
    }

    private fun performVibration() {
        if (!prefs.getBoolean(PREF_VIBRATION_ENABLED, false)) return
        
        val strength = when (prefs.getString(PREF_VIBRATION_STRENGTH, "medium")) {
            "weak" -> 20
            "medium" -> 40
            "strong" -> 60
            else -> 40
        }.toLong()

        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(strength, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(strength)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                counter++
                updateCounter()
                true
            }
            R.id.action_remove -> {
                counter--
                updateCounter()
                true
            }
            R.id.action_reset -> {
                showResetConfirmationDialog()
                true
            }
            R.id.action_settings -> {
                showSettingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateDialogTheme(dialog: AlertDialog) {
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)
        val rootView = dialog.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
        rootView?.setBackgroundColor(Color.BLACK)
        updateViewColors(rootView, Color.WHITE)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
        val titleView = dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)
        titleView?.setTextColor(Color.WHITE)
        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.setTextColor(Color.WHITE)
        dialog.listView?.let { listView ->
            listView.setBackgroundColor(Color.BLACK)
            for (i in 0 until listView.count) {
                (listView.getChildAt(i) as? TextView)?.setTextColor(Color.WHITE)
            }
        }
    }

    private fun updateViewColors(view: View?, textColor: Int) {
        if (view == null) return
        
        when (view) {
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    updateViewColors(view.getChildAt(i), textColor)
                }
            }
            is TextView -> view.setTextColor(textColor)
            is RadioButton -> view.setTextColor(textColor)
            is SwitchCompat -> view.setTextColor(textColor)
            is CheckBox -> view.setTextColor(textColor)
        }
    }

    private fun showDialog(dialog: AlertDialog) {
        dialog.setOnShowListener { updateDialogTheme(dialog) }
        dialog.show()
    }

    private fun showSettingsDialog() {
        val items = arrayOf(
            "Sayaç Ayarları",
            "Titreşim Ayarları",
            "Hakkında"
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Ayarlar")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showCounterSettingsDialog()
                    1 -> showVibrationSettingsDialog()
                    2 -> showAboutDialog()
                }
            }
            .setNegativeButton("Kapat", null)
            .create()

        showDialog(dialog)
    }

    private fun showResetConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Sayacı Sıfırla")
            .setMessage("Sayacı sıfırlamak istediğinizden emin misiniz?")
            .setPositiveButton("Sıfırla") { _, _ ->
                counter = 0
                updateCounter()
            }
            .setNegativeButton("Vazgeç", null)
            .create()

        showDialog(dialog)
    }

    private fun showAboutDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Hakkında")
            .setMessage("Tüm hakları saklıdır\nBayt")
            .setPositiveButton("Tamam", null)
            .create()

        showDialog(dialog)
    }

    private fun showCounterSettingsDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_counter_settings, null)
        
        // Yazı boyutu seçimi
        val fontSizeGroup = view.findViewById<RadioGroup>(R.id.fontSizeGroup)
        when (prefs.getString(PREF_FONT_SIZE, "medium")) {
            "small" -> fontSizeGroup.check(R.id.sizeSmall)
            "medium" -> fontSizeGroup.check(R.id.sizeMedium)
            "large" -> fontSizeGroup.check(R.id.sizeLarge)
        }

        // Yazı rengi seçimi
        val colorGroup = view.findViewById<RadioGroup>(R.id.colorGroup)
        when (prefs.getInt(PREF_FONT_COLOR, Color.WHITE)) {
            Color.WHITE -> colorGroup.check(R.id.colorWhite)
            Color.GRAY -> colorGroup.check(R.id.colorGray)
            Color.BLUE -> colorGroup.check(R.id.colorBlue)
        }

        // Konum seçimi
        val positionGroup = view.findViewById<RadioGroup>(R.id.positionGroup)
        when (prefs.getString(PREF_COUNTER_POSITION, "center")) {
            "top" -> positionGroup.check(R.id.positionTop)
            "center" -> positionGroup.check(R.id.positionCenter)
            "bottom" -> positionGroup.check(R.id.positionBottom)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Sayaç Ayarları")
            .setView(view)
            .setPositiveButton("Kaydet") { _, _ ->
                // Yazı boyutunu kaydet
                val fontSize = when (fontSizeGroup.checkedRadioButtonId) {
                    R.id.sizeSmall -> "small"
                    R.id.sizeMedium -> "medium"
                    R.id.sizeLarge -> "large"
                    else -> "medium"
                }

                // Yazı rengini kaydet
                val fontColor = when (colorGroup.checkedRadioButtonId) {
                    R.id.colorWhite -> Color.WHITE
                    R.id.colorGray -> Color.GRAY
                    R.id.colorBlue -> Color.BLUE
                    else -> Color.WHITE
                }

                // Konumu kaydet
                val position = when (positionGroup.checkedRadioButtonId) {
                    R.id.positionTop -> "top"
                    R.id.positionCenter -> "center"
                    R.id.positionBottom -> "bottom"
                    else -> "center"
                }

                val editor = prefs.edit()
                editor.putString(PREF_FONT_SIZE, fontSize)
                editor.putInt(PREF_FONT_COLOR, fontColor)
                editor.putString(PREF_COUNTER_POSITION, position)
                editor.apply()
                
                applySettings()
            }
            .setNegativeButton("İptal", null)
            .create()

        showDialog(dialog)
    }

    private fun showVibrationSettingsDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_vibration_settings, null)
        
        // Titreşim açık/kapalı
        val enableSwitch = view.findViewById<SwitchCompat>(R.id.enableVibration)
        enableSwitch.isChecked = prefs.getBoolean(PREF_VIBRATION_ENABLED, false)

        // Titreşim şiddeti
        val strengthGroup = view.findViewById<RadioGroup>(R.id.strengthGroup)
        when (prefs.getString(PREF_VIBRATION_STRENGTH, "medium")) {
            "weak" -> strengthGroup.check(R.id.strengthLight)
            "medium" -> strengthGroup.check(R.id.strengthMedium)
            "strong" -> strengthGroup.check(R.id.strengthStrong)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Titreşim Ayarları")
            .setView(view)
            .setPositiveButton("Kaydet") { _, _ ->
                val editor = prefs.edit()
                editor.putBoolean(PREF_VIBRATION_ENABLED, enableSwitch.isChecked)
                editor.putString(PREF_VIBRATION_STRENGTH, when (strengthGroup.checkedRadioButtonId) {
                    R.id.strengthLight -> "weak"
                    R.id.strengthMedium -> "medium"
                    R.id.strengthStrong -> "strong"
                    else -> "medium"
                })
                editor.apply()
            }
            .setNegativeButton("İptal", null)
            .create()

        showDialog(dialog)
    }

    // Aktivite duraklatıldığında sayaç değerini kaydet
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PREF_COUNTER, counter)
    }
    
    private fun updateCounter() {
        counterText.text = counter.toString()
        prefs.edit().putInt(PREF_COUNTER, counter).apply()
    }
}
