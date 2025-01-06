package com.example.tapcounter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
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
import java.util.*

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
        
        // Vibrator servisini başlat ve kontrol et
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() != true) {
            // Titreşim özelliği yoksa veya çalışmıyorsa, ayarı kapat
            getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("vibration_enabled", false)
                .apply()
        }
        
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
        val settingsPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        // Yazı boyutunu ayarla
        val sizePosition = settingsPrefs.getInt("counter_size", 1)
        val fontSize = when (sizePosition) {
            0 -> 24f  // Küçük
            1 -> 36f  // Normal
            2 -> 48f  // Büyük
            3 -> 60f  // Çok Büyük
            else -> 36f
        }
        counterText.textSize = fontSize

        // Yazı rengini ayarla
        val colorPosition = settingsPrefs.getInt("counter_color", 0)
        val color = when (colorPosition) {
            0 -> Color.WHITE
            1 -> Color.RED
            2 -> Color.GREEN
            3 -> Color.BLUE
            4 -> Color.YELLOW
            else -> Color.WHITE
        }
        counterText.setTextColor(color)

        // Konumu ayarla
        val positionPosition = settingsPrefs.getInt("counter_position", 0)
        val params = counterText.layoutParams as ConstraintLayout.LayoutParams
        when (positionPosition) {
            0 -> { // Orta
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                params.verticalBias = 0.5f
                params.horizontalBias = 0.5f
            }
            1 -> { // Sol Üst
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                params.rightToRight = ConstraintLayout.LayoutParams.UNSET
                params.verticalBias = 0.1f
                params.horizontalBias = 0.1f
            }
            2 -> { // Sağ Üst
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                params.leftToLeft = ConstraintLayout.LayoutParams.UNSET
                params.verticalBias = 0.1f
                params.horizontalBias = 0.9f
            }
            3 -> { // Sol Alt
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.topToTop = ConstraintLayout.LayoutParams.UNSET
                params.rightToRight = ConstraintLayout.LayoutParams.UNSET
                params.verticalBias = 0.9f
                params.horizontalBias = 0.1f
            }
            4 -> { // Sağ Alt
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                params.topToTop = ConstraintLayout.LayoutParams.UNSET
                params.leftToLeft = ConstraintLayout.LayoutParams.UNSET
                params.verticalBias = 0.9f
                params.horizontalBias = 0.9f
            }
        }
        counterText.layoutParams = params

        // Arka plan rengini siyah yap
        window.decorView.setBackgroundColor(Color.BLACK)
        findViewById<View>(android.R.id.content).setBackgroundColor(Color.BLACK)
    }

    private fun performVibration() {
        val settingsPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!settingsPrefs.getBoolean("vibration_enabled", false)) return
        
        // Vibrator servisini kontrol et
        if (vibrator == null) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        
        // Titreşim özelliği var mı kontrol et
        if (vibrator?.hasVibrator() != true) {
            // Titreşim özelliği yoksa, ayarı kapat
            settingsPrefs.edit().putBoolean("vibration_enabled", false).apply()
            return
        }
        
        val intensity = settingsPrefs.getInt("vibration_intensity", 50)
        // 0-100 arasındaki intensity değerini 10-100ms arasına map ediyoruz
        val duration = (intensity * 0.9 + 10).toLong()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }
        } catch (e: Exception) {
            // Hata durumunda titreşimi kapat
            settingsPrefs.edit().putBoolean("vibration_enabled", false).apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                incrementCounter()
                true
            }
            R.id.action_remove -> {
                decrementCounter()
                true
            }
            R.id.action_reset -> {
                showResetConfirmationDialog()
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_save -> {
                showSaveDialog()
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
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_settings)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val switchVibration = dialog.findViewById<Switch>(R.id.switchVibration)
        val btnCounterSettings = dialog.findViewById<Button>(R.id.btnCounterSettings)
        val btnSaved = dialog.findViewById<Button>(R.id.btnSaved)
        val btnAbout = dialog.findViewById<Button>(R.id.btnAbout)

        // Titreşim ayarını yükle
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        switchVibration.isChecked = prefs.getBoolean("vibration_enabled", true)

        // Titreşim switch listener
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("vibration_enabled", isChecked).apply()
        }

        // Sayaç ayarları butonu
        btnCounterSettings.setOnClickListener {
            dialog.dismiss()
            showCounterSettingsDialog()
        }

        // Kaydedilenler butonu
        btnSaved.setOnClickListener {
            dialog.dismiss()
            showSavedCountersDialog()
        }

        // Hakkında butonu
        btnAbout.setOnClickListener {
            dialog.dismiss()
            showAboutDialog()
        }

        dialog.show()
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

    private fun showSaveDialog() {
        val editText = EditText(this).apply {
            hint = "Sayaç adını girin"
            inputType = InputType.TYPE_CLASS_TEXT
            setHintTextColor(resources.getColor(android.R.color.darker_gray, theme))
            setTextColor(resources.getColor(android.R.color.white, theme))
            background = null
            setPadding(50, 30, 50, 30)
        }

        val container = FrameLayout(this).apply {
            setBackgroundColor(resources.getColor(android.R.color.black, theme))
            addView(editText)
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Sayacı Kaydet")
            .setView(container)
            .setPositiveButton("Kaydet") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotEmpty()) {
                    saveCounter()
                }
            }
            .setNegativeButton("İptal", null)
            .create()
            .apply { 
                setOnShowListener { updateDialogTheme(this) }
                show()
            }
    }

    private fun saveCounter() {
        val prefs = getSharedPreferences("saved_counters", Context.MODE_PRIVATE)
        val timestamp = System.currentTimeMillis()
        val counterValue = "$counter|$timestamp"
        prefs.edit().putString(UUID.randomUUID().toString(), counterValue).apply()
        Toast.makeText(this, "Sayaç kaydedildi!", Toast.LENGTH_SHORT).show()
    }

    private fun showSavedCountersDialog() {
        val intent = Intent(this, SavedCountersActivity::class.java)
        startActivity(intent)
    }

    // Aktivite duraklatıldığında sayaç değerini kaydet
    override fun onPause() {
        super.onPause()
        // Sayaç değerini kaydet
        val prefs = getSharedPreferences("counter", Context.MODE_PRIVATE)
        prefs.edit().putInt("current_value", counter).apply()
    }

    override fun onResume() {
        super.onResume()
        // Sayaç değerini yükle
        val prefs = getSharedPreferences("counter", Context.MODE_PRIVATE)
        counter = prefs.getInt("current_value", 0)
        updateCounter()
        applySettings()
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
    
    private fun incrementCounter() {
        counter++
        updateCounter()
    }
    
    private fun decrementCounter() {
        counter--
        updateCounter()
    }
}
