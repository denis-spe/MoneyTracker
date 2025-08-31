package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.registerScreen.NamesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NamesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            NamesScreen()
        }
    }

    @Test
    fun testDisplayedContentInNamesScreen() {
        // Define expected content
        val nameRegisterScreenId = composeTestRule.activity
            .getString(R.string.nameRegisterScreenId)
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)
        val nameRegisterDescriptionId = composeTestRule.activity
            .getString(R.string.nameRegisterDescriptionId)
        val nameRegisterImg = composeTestRule.activity
            .getString(R.string.name_register_img)
        val nameRegisterFirstNameFieldId = composeTestRule.activity
            .getString(R.string.nameRegisterFirstNameFieldId)
        val nameRegisterFirstNameFieldPlaceholder = composeTestRule.activity
            .getString(R.string.nameRegisterFirstNameFieldPlaceholder)
        val nameRegisterLastNameFieldId = composeTestRule.activity
            .getString(R.string.nameRegisterLastNameFieldId)
        val nameRegisterLastNameFieldPlaceholder = composeTestRule.activity
            .getString(R.string.nameRegisterLastNameFieldPlaceholder)
        val nameRegisterBtnId = composeTestRule.activity
            .getString(R.string.nameRegisterBtnId)
        val nameRegisterBtnText = composeTestRule.activity
            .getString(R.string.name_register_btn_text)
        val nameRegisterPageFlowImg = composeTestRule.activity
            .getString(R.string.name_register_page_flow_img)

        // Assert that the expected content is displayed
        composeTestRule.onNodeWithTag(nameRegisterScreenId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleMoney)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleTracker)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterDescriptionId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(nameRegisterFirstNameFieldId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterFirstNameFieldPlaceholder)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(nameRegisterLastNameFieldId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterLastNameFieldPlaceholder)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(nameRegisterBtnId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterBtnText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterPageFlowImg)
            .assertExists()
            .assertIsDisplayed()

    }
}