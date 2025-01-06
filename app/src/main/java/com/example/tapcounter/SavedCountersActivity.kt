package com.example.tapcounter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class SavedCountersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedCountersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_counters)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kaydedilen Sayaçlar"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.black)))
        window.statusBarColor = resources.getColor(android.R.color.black)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("saved_counters", Context.MODE_PRIVATE)
        val savedCounters = mutableListOf<SavedCounter>()
        
        prefs.all.forEach { (id, value) ->
            val parts = value.toString().split("|")
            if (parts.size == 2) {
                try {
                    val counterValue = parts[0].toInt()
                    val timestamp = parts[1].toLong()
                    savedCounters.add(SavedCounter(id, counterValue, timestamp))
                } catch (e: Exception) {
                    // Hatalı veriyi atla
                }
            }
        }

        savedCounters.sortByDescending { it.timestamp }

        adapter = SavedCountersAdapter(savedCounters) { counter ->
            prefs.edit().remove(counter.id).apply()
            adapter.removeCounter(counter)
        }
        recyclerView.adapter = adapter

        // Eğer liste boşsa mesaj göster
        if (savedCounters.isEmpty()) {
            findViewById<TextView>(R.id.emptyText).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.emptyText).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

data class SavedCounter(
    val id: String,
    val value: Int,
    val timestamp: Long
)

class SavedCountersAdapter(
    private val counters: MutableList<SavedCounter>,
    private val onDeleteClick: (SavedCounter) -> Unit
) : RecyclerView.Adapter<SavedCountersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textValue: TextView = view.findViewById(R.id.textValue)
        val textDate: TextView = view.findViewById(R.id.textDate)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_counter_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val counter = counters[position]
        holder.textValue.text = counter.value.toString()
        
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        holder.textDate.text = dateFormat.format(Date(counter.timestamp))
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(counter)
        }
    }

    override fun getItemCount() = counters.size

    fun removeCounter(counter: SavedCounter) {
        val position = counters.indexOf(counter)
        if (position != -1) {
            counters.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
