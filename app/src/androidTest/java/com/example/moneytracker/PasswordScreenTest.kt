package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordRegistrationScreen
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(FirebaseAuthModule::class)
@RunWith(AndroidJUnit4::class)
class PasswordScreenTest : TestBase(screenComposable = {
    PasswordRegistrationScreen()
}) {

    /**
     * Test for screenId
     */
    @Test
    fun testScreenId() {
        val screenId = composeTestRule.activity
            .getString(R.string.passwordRegisterScreenId)
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
            .getString(R.string.passwordRegisterDescriptionId)
        val loginDescriptionText = composeTestRule.activity
            .getString(R.string.password_register_desc_text)
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
        val passwordRegisterPasswordFieldId = composeTestRule.activity
            .getString(R.string.passwordRegisterPasswordFieldId)
        val passwordRegisterPasswordFieldPlaceholder = composeTestRule.activity
            .getString(R.string.passwordRegisterPasswordFieldPlaceholder)
        val passwordConfirmRegisterPasswordFieldId = composeTestRule.activity
            .getString(R.string.passwordConfirmRegisterPasswordFieldId)
        val passwordConfirmRegisterPasswordFieldPlaceholder = composeTestRule.activity
            .getString(R.string.passwordConfirmRegisterPasswordFieldPlaceholder)
        val passwordVisibilityBtn = composeTestRule.activity
            .getString(R.string.passwordVisibilityBtnId)



        composeTestRule.onNodeWithTag(passwordRegisterPasswordFieldId)
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(passwordRegisterPasswordFieldPlaceholder)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(passwordConfirmRegisterPasswordFieldId)
            .assertExists()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(passwordConfirmRegisterPasswordFieldPlaceholder)
            .assertIsDisplayed()


        // Perform text input on the password field
        composeTestRule.onNodeWithTag(passwordRegisterPasswordFieldId)
            .performTextInput("password123")

        // Perform click on the password visibility button
        composeTestRule.onAllNodes(
            hasTestTag(passwordVisibilityBtn)
        ).onFirst().performClick()



        composeTestRule.onNodeWithTag(passwordRegisterPasswordFieldId)
            .assertTextEquals("password123")

        // Perform text input on the password field
        composeTestRule.onNodeWithTag(passwordConfirmRegisterPasswordFieldId)
            .performTextInput("password123")

        // Perform click on the password visibility button
        composeTestRule.onAllNodes(
            hasTestTag(passwordVisibilityBtn)
        ).onLast().performClick()


        composeTestRule.onNodeWithTag(passwordConfirmRegisterPasswordFieldId)
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
            .getString(R.string.passwordRegisterBtnId)
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