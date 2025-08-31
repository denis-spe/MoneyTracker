package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.registerScreen.EmailScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            EmailScreen()
        }
    }

    @Test
    fun testDisplayedContentInEmail() {
        // Define expected content
        val emailRegisterScreenId = composeTestRule.activity
            .getString(R.string.emailRegisterScreenId)
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)
        val emailRegisterDescriptionId = composeTestRule.activity
            .getString(R.string.emailRegisterDescriptionId)
        val emailRegisterImg = composeTestRule.activity
            .getString(R.string.email_register_img)
        val emailRegisterEmailFieldId = composeTestRule.activity
            .getString(R.string.emailRegisterEmailFieldId)
        val emailRegisterEmailFieldPlaceholder = composeTestRule.activity
            .getString(R.string.emailRegisterEmailFieldPlaceholder)
        val emailRegisterBtnId = composeTestRule.activity
            .getString(R.string.emailRegisterBtnId)
        val emailRegisterBtnText = composeTestRule.activity
            .getString(R.string.email_register_btn_text)
        val emailRegisterPageFlowImg = composeTestRule.activity
            .getString(R.string.email_register_page_flow_img)


        composeTestRule.onNodeWithTag(emailRegisterScreenId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleMoney)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleTracker)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterDescriptionId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterEmailFieldId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterEmailFieldPlaceholder)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterBtnId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterBtnText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterPageFlowImg)
            .assertExists()
            .assertIsDisplayed()

    }
}