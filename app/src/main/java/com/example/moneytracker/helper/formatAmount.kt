// Glory be to the LORD our GOD
package com.example.moneytracker.helper

import android.icu.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow


fun Long.formatToAmount(): String {
    val locale = Locale.getDefault()
    val numberFormat = NumberFormat.getCurrencyInstance(locale)
    val symbol = numberFormat.currency?.symbol ?: "$"

    if (this < 1_000_000) {
        val amount = toDouble()
        return numberFormat.format(amount).split(".")[0]
    }
    val suffixes = charArrayOf('M', 'B', 'T') // M for Million, etc.
    val formatter = DecimalFormat("#.0")
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return "$symbol${formatter.format(this / 1000.0.pow(exp.toDouble())) + suffixes[exp - 1]}"
}

fun Float.formatToAmount(): String {
    return this.toLong().formatToAmount()
}

fun Double.formatToAmount(): String {
    return this.toLong().formatToAmount()
}

