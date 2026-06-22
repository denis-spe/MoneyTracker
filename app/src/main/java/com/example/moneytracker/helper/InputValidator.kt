// Glory be to the LORD GOD of hosts
package com.example.moneytracker.helper

import androidx.compose.runtime.MutableState

fun MutableState<InputState>.isAmountValid(amount: Double?): Boolean {
    this.value = if (amount == null) {
        InputState.Error("Amount cannot be empty")
    } else if (amount <= 0) {
        InputState.Error("Amount cannot be zero or negative")
    } else {
        InputState.Success
    }

    return this.value is InputState.Success
}

fun MutableState<InputState>.isLabelValid(label: String?): Boolean {
    this.value = if (label == null) {
        InputState.Error("Label cannot be empty")
    } else if (label.isBlank()) {
        InputState.Error("Label cannot be blank")
    } else {
        InputState.Success
    }

    return this.value is InputState.Success
}

