package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PasswordScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            PasswordScreen()
        }
    }

    @Test
    fun testDisplayedContentInPasswordScreen() {

    }
}