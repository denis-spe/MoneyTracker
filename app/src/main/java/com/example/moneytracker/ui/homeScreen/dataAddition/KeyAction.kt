// Glory be to the name LORD our GOD and to his son Jesus
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.text.input.KeyboardActionHandler

class KeyAction(val handler: () -> Unit) : KeyboardActionHandler {
    override fun onKeyboardAction(performDefaultAction: () -> Unit) {
        performDefaultAction()
        handler()
    }
}