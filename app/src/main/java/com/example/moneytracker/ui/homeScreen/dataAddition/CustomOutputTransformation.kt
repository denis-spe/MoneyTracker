// Praise be the LORD of host
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import java.text.NumberFormat
import java.util.Locale

class CustomOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        val text = asCharSequence().toString()
        val locale = Locale.getDefault()
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        val amount = text.toDoubleOrNull() ?: 0.0
        var formattedAmount = numberFormat.format(amount)
        formattedAmount = formattedAmount.replace(
            numberFormat.currency?.symbol ?: "$",
            ""
        )
        formattedAmount = formattedAmount.split(".")[0]


        replace(
            0,
            length,
            formattedAmount
        )
    }
}