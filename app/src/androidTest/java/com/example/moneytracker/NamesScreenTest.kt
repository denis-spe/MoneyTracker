package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.registerScreen.NamesRegistrationScreen
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(FirebaseAuthModule::class)
@RunWith(AndroidJUnit4::class)
class NamesScreenTest : TestBase(screenComposable = {
    NamesRegistrationScreen()
}) {

    /**
     * Test for screenId
     */
    @Test
    fun testScreenId() {
        val screenId = composeTestRule.activity
            .getString(R.string.nameRegisterScreenId)
        composeTestRule.onNodeWithTag(screenId)
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Test for screen title
     */
    @Test
    fun testScreenTitle() {
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)

        composeTestRule.onNodeWithText(titleMoney)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(titleTracker)
            .assertIsDisplayed()
    }

    /**
     * Test for screen description
     */
    @Test
    fun testScreenDescription() {
        val nameRegisterDescriptionId = composeTestRule.activity
            .getString(R.string.nameRegisterDescriptionId)
        val nameRegisterImg = composeTestRule.activity
            .getString(R.string.screen_logo)
        val nameRegisterDescription = composeTestRule.activity
            .getString(R.string.name_register_desc_text)

        composeTestRule.onNodeWithTag(nameRegisterDescriptionId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(nameRegisterImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterDescription)
            .assertIsDisplayed()
    }

    /**
     * Test for screen input fields
     */
    @Test
    fun testScreenInputs() {
        val nameRegisterFirstNameFieldId = composeTestRule.activity
            .getString(R.string.nameRegisterFirstNameFieldId)
        val nameRegisterFirstNameFieldPlaceholder = composeTestRule.activity
            .getString(R.string.nameRegisterFirstNameFieldPlaceholder)
        val nameRegisterLastNameFieldId = composeTestRule.activity
            .getString(R.string.nameRegisterLastNameFieldId)
        val nameRegisterLastNameFieldPlaceholder = composeTestRule.activity
            .getString(R.string.nameRegisterLastNameFieldPlaceholder)

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
    }

    /**
     * Test for screen page flow image
     */
    @Test
    fun testScreenPageFlow() {
        val nameRegisterPageFlowImg = composeTestRule.activity
            .getString(R.string.pageFlowId)

        composeTestRule.onNodeWithTag(nameRegisterPageFlowImg)
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Test for screen buttons
     */
    @Test
    fun testScreenButtons() {
        val nameRegisterBtnId = composeTestRule.activity
            .getString(R.string.nameRegisterBtnId)
        val nameRegisterBtnText = composeTestRule.activity
            .getString(R.string.name_register_btn_text)

        composeTestRule.onNodeWithTag(nameRegisterBtnId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(nameRegisterBtnText)
            .assertIsDisplayed()
    }
}