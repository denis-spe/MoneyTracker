package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.screenManager.ScreenManager
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(FirebaseAuthModule::class)
@RunWith(AndroidJUnit4::class)
class ScreenManagerTest : TestBase(
    screenComposable = {
        ScreenManager(rememberNavController())
    }) {

    @Test
    fun startUpScreenTest() {
        val screenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)
        composeTestRule.onNodeWithTag(screenId)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun testScreenFlowThroughGoogle(){
        val screenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)
        val googleScreenId = composeTestRule.activity
            .getString(R.string.googleScreenId)
        val googleButtonId = composeTestRule.activity
            .getString(R.string.startupGoogleBtnId)
        val backButtonId = composeTestRule.activity
            .getString(R.string.authBackBtnId)

        composeTestRule.onNodeWithTag(screenId)
            .assertExists()
            .assertIsDisplayed()

        // Preform a click on google button
        composeTestRule.onNodeWithTag(googleButtonId).performClick()

        // Check if google page is displayed
        composeTestRule.onNodeWithTag(googleScreenId).assertExists()

        // Click back button to go back the startUp page.
        composeTestRule.onNodeWithTag(backButtonId).performClick()
    }


    @Test
    fun testIfLoginScreenWillBeDisplayedAfterIncorrectInformation() {
        val screenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)
        val loginButtonId = composeTestRule.activity
            .getString(R.string.mailLoginBtnId)
        val loginScreenId = composeTestRule.activity
            .getString(R.string.loginScreenId)
        val loginEmailFieldId = composeTestRule.activity
            .getString(R.string.loginEmailFieldId)
        val loginPasswordFieldId = composeTestRule.activity
            .getString(R.string.loginPasswordFieldId)
        val loginNextScreenButtonId = composeTestRule.activity
            .getString(R.string.loginBtnId)
        val startupMailBtnId = composeTestRule.activity
            .getString(R.string.startupMailBtnId)
        val mailScreenId = composeTestRule.activity
            .getString(R.string.mailScreenId)

        // Starting from startUp page
        composeTestRule.onNodeWithTag(screenId).assertExists()

        // Preform a click on mail button
        composeTestRule.onNodeWithTag(startupMailBtnId).performClick()

        // Check if mail page is displayed
        composeTestRule.onNodeWithTag(mailScreenId).assertExists()

        // Preform a click on login button
        composeTestRule.onNodeWithTag(loginButtonId).performClick()
//
//        // Check if login page is displayed
        composeTestRule.onNodeWithTag(loginScreenId).assertExists()

        // Entering incorrect information
        composeTestRule.onNodeWithTag(loginEmailFieldId)
            .performTextInput("denis07gmail.com")
        composeTestRule.onNodeWithTag(loginPasswordFieldId)
            .performTextInput("4553")
        composeTestRule.onNodeWithTag(loginNextScreenButtonId).performClick()
        composeTestRule.onNodeWithTag(loginScreenId).assertExists()
    }

    @Test
    fun testScreenFlowThroughMailByRegistering() {
        val screenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)
        val mailScreenId = composeTestRule.activity
            .getString(R.string.mailScreenId)
        val mailButtonId = composeTestRule.activity
            .getString(R.string.startupMailBtnId)
        val mailRegisterBtnId = composeTestRule.activity
            .getString(R.string.mailRegisterBtnId)
        val nameRegisterScreenId = composeTestRule.activity
            .getString(R.string.nameRegisterScreenId)
        val nameRegisterFirstNameFieldId = composeTestRule.activity
            .getString(R.string.nameRegisterFirstNameFieldId)
        val nameRegisterLastNameFieldId = composeTestRule.activity
            .getString(R.string.nameRegisterLastNameFieldId)
        val nameRegisterBtnId = composeTestRule.activity
            .getString(R.string.nameRegisterBtnId)
        val loginNextScreenButtonId = composeTestRule.activity
            .getString(R.string.loginBtnId)

        composeTestRule.onNodeWithTag(screenId).assertExists()
        composeTestRule.onNodeWithTag(mailButtonId).performClick()
        composeTestRule.onNodeWithTag(mailScreenId).assertExists()
        composeTestRule.onNodeWithTag(mailRegisterBtnId).performClick()
        composeTestRule.onNodeWithTag(nameRegisterScreenId).assertExists()
        composeTestRule.onNodeWithTag(nameRegisterFirstNameFieldId)
            .performTextInput("Denis")
        composeTestRule.onNodeWithTag(nameRegisterLastNameFieldId)
            .performTextInput("Brian")
        composeTestRule.onNodeWithTag(nameRegisterBtnId).performClick()

    }
}