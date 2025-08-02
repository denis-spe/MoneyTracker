package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.googleScreen.GoogleScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            GoogleScreen()
        }
    }

    @Test
    fun testTextInGoogle() {
        val googleScreenId = composeTestRule.activity
            .getString(R.string.googleScreenId)
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)

        val googleInstructionText = composeTestRule.activity
            .getString(R.string.google_instruction_text)
        val googleDescriptionId = composeTestRule.activity
            .getString(R.string.googleDescriptionId)
        val googleAccountText = composeTestRule.activity
            .getString(R.string.google_account_text)
        val googleLoginBtnId = composeTestRule.activity
            .getString(R.string.googleLoginBtnId)
        val googleLoginText = composeTestRule.activity
            .getString(R.string.google_login_text)
        val googleRegisterBtnId = composeTestRule.activity
            .getString(R.string.googleRegisterBtnId)
        val googleRegisterText = composeTestRule.activity
            .getString(R.string.google_register_text)
        val orText = composeTestRule.activity
            .getString(R.string.or_text)

        // Images on google screen
        val googleLogo = composeTestRule.activity
            .getString(R.string.google_img)
        val googleGooglePageFlow = composeTestRule.activity
            .getString(R.string.google_page_flow_img)


        // Check if there is the startUpScreen
        composeTestRule.onNodeWithTag(googleScreenId).assertExists()

        // Check if the description is displayed
        composeTestRule.onNodeWithTag(googleDescriptionId).assertExists()

        // Check if the title is displayed
        composeTestRule.onNodeWithText(titleMoney).assertIsDisplayed()
        composeTestRule.onNodeWithText(titleTracker).assertIsDisplayed()

        // Check if the introduction text is displayed
        composeTestRule.onNodeWithText(googleInstructionText).assertIsDisplayed()

        // Check if the google account text is displayed
        composeTestRule.onNodeWithText(googleAccountText).assertIsDisplayed()

        // Check if the google login button is displayed
        composeTestRule.onNodeWithTag(googleLoginBtnId).assertIsDisplayed()
        composeTestRule.onNodeWithText(googleLoginText).assertIsDisplayed()

        // Check if the google register button is displayed
        composeTestRule.onNodeWithTag(googleRegisterBtnId).assertIsDisplayed()
        composeTestRule.onNodeWithText(googleRegisterText).assertIsDisplayed()

        // Check if the or text is displayed
        composeTestRule.onNodeWithText(orText).assertIsDisplayed()

        // Check if the images are displayed
        composeTestRule.onNodeWithTag(googleLogo).assertExists()
        composeTestRule.onNodeWithTag(googleGooglePageFlow).assertExists()

    }
}