package com.example.moneytracker.helper

sealed class InputState {
    object Initial : InputState()
    data class Error(val message: String) : InputState()
    object Success : InputState()
}