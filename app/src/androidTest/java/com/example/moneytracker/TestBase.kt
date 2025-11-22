package com.example.moneytracker

import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

open class TestBase(private val screenComposable: @Composable () -> Unit) {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.setContent {
                screenComposable()
            }
        }
    }
}
