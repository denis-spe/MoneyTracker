// Praise be the LORD of host
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.insert

class CustomOutputTransformation : OutputTransformation {
    @OptIn(ExperimentalFoundationApi::class)
    override fun TextFieldBuffer.transformOutput() {
        if (length == 4) insert(1, ",")
        if (originalText.length == 5) insert(2, ",")
        if (originalText.length == 6) insert(3, ",")
        if (originalText.length == 7) {
            insert(1, ",")
            insert(5, ",")
        }

        if (originalText.length == 8) {
            insert(2, ",")
            insert(6, ",")
        }
        if (originalText.length == 9) {
            insert(3, ",")
            insert(7, ",")
        }
        if (originalText.length == 10) {
            insert(1, ",")
            insert(5, ",")
            insert(9, ",")
        }
        if (originalText.length == 11) {
            insert(2, ",")
            insert(6, ",")
            insert(10, ",")
        }

        if (originalText.length == 12) {
            insert(3, ",")
            insert(7, ",")
            insert(11, ",")
        }

        if (originalText.length == 13) {
            insert(1, ",")
            insert(5, ",")
            insert(9, ",")
            insert(13, ",")
        }
        if (originalText.length == 14) {
            insert(2, ",")
            insert(6, ",")
            insert(10, ",")
            insert(14, ",")
        }
        if (originalText.length == 15) {
            insert(3, ",")
            insert(7, ",")
            insert(11, ",")
            insert(15, ",")
        }
        if (originalText.length == 16) {
            insert(1, ",")
            insert(5, ",")
            insert(9, ",")
            insert(13, ",")
            insert(17, ",")
        }
    }
}