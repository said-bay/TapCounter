package com.example.tapcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    private var counter = 0
    private lateinit var counterText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // AMOLED uyumlu tam siyah tema için sistem UI ayarları
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )
        
        counterText = findViewById(R.id.counterText)
        
        // Ana layout'a tıklama olayını ekle
        findViewById<ConstraintLayout>(R.id.mainLayout).setOnClickListener {
            counter++
            updateCounter()
        }
    }
    
    private fun updateCounter() {
        counterText.text = counter.toString()
    }
}
