// Bless be the LORD our GOD

package com.example.moneytracker.helper

import android.util.Patterns

data class Validator(
    val validator: Boolean,
    val errorMessage: String
)

val CharSequence.isEmailValid: Validator
    get() {
        lateinit var validator: Validator

        if (this.isEmpty()) {
            validator = Validator(
                validator = false,
                errorMessage = "Email cannot be empty"
            )
        } else if (!Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
            validator = Validator(
                validator = false,
                errorMessage = "Invalid email"
            )
        } else {
            validator = Validator(
                validator = true,
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
                validator = false,
                errorMessage = "Password cannot be empty"
            )
        } else if (this.length < 8) {
            validator = Validator(
                validator = false,
                errorMessage = "Password must be at least 8 characters"
            )
        } else {
            validator = Validator(
                validator = true,
                errorMessage = ""
            )
        }
        return validator
    }