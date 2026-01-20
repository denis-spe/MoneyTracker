// Praise be the LORD of host
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.insert

class CustomOutputTransformation : OutputTransformation {
    @OptIn(ExperimentalFoundationApi::class)
    override fun TextFieldBuffer.transformOutput() {
        val wholeNumber = originalText.split(".")[0]

        if (wholeNumber.length == 4) insert(1, ",")
        if (wholeNumber.length == 5) insert(2, ",")
        if (wholeNumber.length == 6) insert(3, ",")
        if (wholeNumber.length == 7) {
            insert(1, ",")
            insert(5, ",")
        }

        if (wholeNumber.length == 8) {
            insert(2, ",")
            insert(6, ",")
        }
        if (wholeNumber.length == 9) {
            insert(3, ",")
            insert(7, ",")
        }
        if (wholeNumber.length == 10) {
            insert(1, ",")
            insert(5, ",")
            insert(9, ",")
        }
        if (wholeNumber.length == 11) {
            insert(2, ",")
            insert(6, ",")
            insert(10, ",")
        }

        if (wholeNumber.length == 12) {
            insert(3, ",")
            insert(7, ",")
            insert(11, ",")
        }

        if (wholeNumber.length == 13) {
            insert(1, ",")
            insert(5, ",")
            insert(9, ",")
            insert(13, ",")
        }
        if (wholeNumber.length == 14) {
            insert(2, ",")
            insert(6, ",")
            insert(10, ",")
            insert(14, ",")
        }
        if (wholeNumber.length == 15) {
            insert(3, ",")
            insert(7, ",")
            insert(11, ",")
            insert(15, ",")
        }
        if (wholeNumber.length == 16) {
            insert(1, ",")
            insert(5, ",")
            insert(9, ",")
            insert(13, ",")
            insert(17, ",")
        }
    }
}