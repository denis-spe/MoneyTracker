package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.registerScreen.EmailRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.RegisterViewModel
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(FirebaseAuthModule::class)
@RunWith(AndroidJUnit4::class)
class EmailScreenTest : TestBase(screenComposable = {
    val registerViewModel: RegisterViewModel = hiltViewModel()
    EmailRegistrationScreen(viewModel = registerViewModel)
}) {

    /**
     * Test for screenId
     */
    @Test
    fun testScreenId() {
        val screenId = composeTestRule.activity
            .getString(R.string.emailRegisterScreenId)
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
        val emailRegisterDescriptionId = composeTestRule.activity
            .getString(R.string.emailRegisterDescriptionId)
        val emailRegisterImg = composeTestRule.activity
            .getString(R.string.screen_logo)
        val emailRegisterDescription = composeTestRule.activity
            .getString(R.string.email_register_desc_text)

        composeTestRule.onNodeWithTag(emailRegisterDescriptionId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(emailRegisterImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterDescription)
            .assertIsDisplayed()
    }

    /**
     * Test for screen input fields
     */
    @Test
    fun testScreenInputs() {
        val emailRegisterEmailFieldId = composeTestRule.activity
            .getString(R.string.emailRegisterEmailFieldId)
        val emailRegisterEmailFieldPlaceholder = composeTestRule.activity
            .getString(R.string.emailRegisterEmailFieldPlaceholder)

        composeTestRule.onNodeWithTag(emailRegisterEmailFieldId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterEmailFieldPlaceholder)
            .assertIsDisplayed()

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
        val emailRegisterBtnId = composeTestRule.activity
            .getString(R.string.emailRegisterBtnId)
        val emailRegisterBtnText = composeTestRule.activity
            .getString(R.string.email_register_btn_text)

        composeTestRule.onNodeWithTag(emailRegisterBtnId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(emailRegisterBtnText)
            .assertIsDisplayed()
    }
}