package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.screenManager.ScreenManager
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenManagerTest : TestBase(screenComposable = {
    val navController = rememberNavController()
    ScreenManager(navController)
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
    fun testScreenFlowThroughMailLogin(){
        val screenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)

        val mailScreenId = composeTestRule.activity
            .getString(R.string.mailScreenId)
        val mailButtonId = composeTestRule.activity
            .getString(R.string.startupMailBtnId)

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

        val backButtonId = composeTestRule.activity
            .getString(R.string.authBackBtnId)

        // Starting from startUp page
        composeTestRule.onNodeWithTag(screenId).assertExists()

        // Preform a click on mail button
        composeTestRule.onNodeWithTag(mailButtonId).performClick()

        // Check if mail page is displayed
        composeTestRule.onNodeWithTag(mailScreenId).assertExists()

        // Preform a click on login button
        composeTestRule.onNodeWithTag(loginButtonId).performClick()

        // Check if login page is displayed
        composeTestRule.onNodeWithTag(loginScreenId).assertExists()

        // Entering wrong information
        composeTestRule.onNodeWithTag(loginEmailFieldId)
            .performTextInput("denis@.com")
        composeTestRule.onNodeWithTag(loginPasswordFieldId)
            .performTextInput("")
        composeTestRule.onNodeWithTag(loginNextScreenButtonId).performClick()
        composeTestRule.onNodeWithTag(loginScreenId).assertExists()

        // Entering correct information
//        composeTestRule.onNodeWithTag("loginEmailField")
//            .performTextInput("denis@gmail.com")
//        composeTestRule.onNodeWithTag("loginPasswordField")
//            .performTextInput("ILoveGod")
//        composeTestRule.onNodeWithTag("loginNextScreenButton").performClick()
//        composeTestRule.onNodeWithTag("HomeScreen").assertExists()
    }
}