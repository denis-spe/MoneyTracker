// Bless be the name of LORD
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.core.text.isDigitsOnly

class CustomInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().isDigitsOnly()) {
            revertAllChanges()
        }

        placeCursorBeforeCharAt(length)
    }
}