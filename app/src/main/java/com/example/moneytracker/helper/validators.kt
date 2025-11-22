// Bless be the LORD our GOD

package com.example.moneytracker.helper

import android.util.Patterns

data class Validator(
    val isValid: Boolean,
    val errorMessage: String
)

val CharSequence.isEmailValid: Validator
    get() {
        lateinit var validator: Validator

        if (this.isEmpty()) {
            validator = Validator(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )
        } else if (!Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
            validator = Validator(
                isValid = false,
                errorMessage = "Invalid email"
            )
        } else {
            validator = Validator(
                isValid = true,
                errorMessage = ""
            )
        }
        return validator
    }

val CharSequence.isPasswordValid: Validator
    get() {
        lateinit var validator: Validator
        if (this.isEmpty()) {
            validator = Validator(
                isValid = false,
                errorMessage = "Password cannot be empty"
            )
        } else if (this.length < 8) {
            validator = Validator(
                isValid = false,
                errorMessage = "Password must be at least eight characters"
            )
        } else {
            validator = Validator(
                isValid = true,
                errorMessage = ""
            )
        }
        return validator
    }