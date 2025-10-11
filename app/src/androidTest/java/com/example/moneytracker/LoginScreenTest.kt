// Bless be the LORD ALMIGHTY

package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest : TestBase(
    screenComposable = {
        LoginScreen()
    }) {

    /**
     * Test for screenId
     */
    @Test
    fun testScreenId() {
        val screenId = composeTestRule.activity
            .getString(R.string.loginScreenId)
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
        val loginDescription = composeTestRule.activity
            .getString(R.string.loginDescriptionId)
        val loginDescriptionText = composeTestRule.activity
            .getString(R.string.login_desc_text)
        val screenLogo = composeTestRule.activity
            .getString(R.string.screen_logo)

        composeTestRule.onNodeWithTag(loginDescription)
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(loginDescriptionText)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(screenLogo)
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Test for screen input fields
     */
    @Test
    fun testScreenInputs() {
        val loginEmailField = composeTestRule.activity
            .getString(R.string.loginEmailFieldId)
        val loginEmailFieldPlaceholder = composeTestRule.activity
            .getString(R.string.loginEmailFieldPlaceholder)
        val loginPasswordField = composeTestRule.activity
            .getString(R.string.loginPasswordFieldId)
        val loginPasswordPlaceholder = composeTestRule.activity
            .getString(R.string.loginPasswordPlaceholder)
        val passwordVisibilityBtn = composeTestRule.activity
            .getString(R.string.passwordVisibilityBtnId)
        val emailClearTextBtn = composeTestRule.activity
            .getString(R.string.emailClearTextBtnId)


        composeTestRule.onNodeWithTag(loginEmailField)
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(loginEmailFieldPlaceholder)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(loginPasswordField)
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(loginPasswordPlaceholder)
            .assertIsDisplayed()

        // Perform text input on the email field
        composeTestRule.onNodeWithTag(loginEmailField)
            .performTextInput("denis@gmail.com")


        composeTestRule.onNodeWithTag(loginEmailField)
            .assertTextEquals("denis@gmail.com")

        // Does emailClearTextBtn exist?
        composeTestRule.onNodeWithTag(emailClearTextBtn)
            .assertExists()
            .assertIsDisplayed()


        // Perform click on the password visibility button
        composeTestRule.onNodeWithTag(passwordVisibilityBtn)
            .performClick()

        // Perform text input on the password field
        composeTestRule.onNodeWithTag(loginPasswordField)
            .performTextInput("password123")

        composeTestRule.onNodeWithTag(loginPasswordField)
            .assertTextEquals("password123")
    }

    /**
     * Test for screen page flow image
     */
    @Test
    fun testScreenPageFlow() {
        val pageFlowImg = composeTestRule.activity
            .getString(R.string.pageFlowId)

        composeTestRule.onNodeWithTag(pageFlowImg)
            .assertExists()
            .assertIsDisplayed()
    }

    /**
     * Test for screen buttons
     */
    @Test
    fun testScreenButtons() {
        val loginButton = composeTestRule.activity
            .getString(R.string.loginBtnId)
        val backButton = composeTestRule.activity
            .getString(R.string.authBackBtnId)


        composeTestRule.onNodeWithTag(loginButton)
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(backButton)
            .assertExists()
            .assertIsDisplayed()
    }
}