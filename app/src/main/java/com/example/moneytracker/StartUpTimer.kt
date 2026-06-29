// Bless be the LORD of hosts the GOD of Isael
package com.example.moneytracker

import android.util.Log

object StartupTimer {
    private val marks = mutableListOf<Pair<String, Long>>()

    fun mark(label: String) {
        val now = System.currentTimeMillis()
        val start = marks.firstOrNull()?.second ?: now
        Log.d("StartupTimer", "[$label] +${now - start}ms")
        marks.add(label to now)
    }

    fun reset() = marks.clear()
}
