package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.startUpScreen.StartUpScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartUpScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            StartUpScreen()
        }
    }

    @Test
    fun testTextInStartUpScreen() {

        val startUpScreenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)
        val introductionText = composeTestRule.activity
            .getString(R.string.introduction_text)
        val introductionSubText = composeTestRule.activity
            .getString(R.string.introduction_subText)

        // Check if there is the startUpScreen
        composeTestRule.onNodeWithTag(startUpScreenId).assertExists()

        // Check if the title is displayed
        composeTestRule.onNodeWithText(titleMoney).assertIsDisplayed()
        composeTestRule.onNodeWithText(titleTracker).assertIsDisplayed()

        // Check if there is the description of the app
        composeTestRule.onNodeWithText(introductionText).assertIsDisplayed()

        composeTestRule.onNodeWithText(introductionSubText).assertExists()

    }

    @Test
    fun testButtonInStartUpScreen(){

        // Get the button text
        val googleText = composeTestRule.activity
            .getString(R.string.google_text)
        val mailText = composeTestRule.activity
            .getString(R.string.google_text)
        val continueGuestText = composeTestRule.activity
            .getString(R.string.continue_guest_text)

        // Get the button
        val googleButton = composeTestRule.activity
            .getString(R.string.googleBtnId)
        val mailButton = composeTestRule.activity
            .getString(R.string.mailBtnId)
        val continueGuestButton = composeTestRule.activity
            .getString(R.string.continueGuestId)

        // Check if the button is displayed
        composeTestRule.onNodeWithTag(googleButton).assertIsDisplayed()
        composeTestRule.onNodeWithTag(mailButton).assertIsDisplayed()
        composeTestRule.onNodeWithTag(continueGuestButton).assertIsDisplayed()

        // Check if the button text are displayed
        composeTestRule.onNodeWithText(googleText).assertIsDisplayed()
        composeTestRule.onNodeWithText(mailText).assertIsDisplayed()
        composeTestRule.onNodeWithText(continueGuestText).assertIsDisplayed()
    }
}