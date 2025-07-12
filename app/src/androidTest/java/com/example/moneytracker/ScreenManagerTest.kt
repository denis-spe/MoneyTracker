package com.example.moneytracker

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.screenManager.ScreenManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenManagerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp(){
        composeTestRule.setContent {
            ScreenManager()
        }
    }

    @Test
    fun startUpScreenTest() {
        // Check if the startUp page is displayed
        composeTestRule.onNodeWithTag("startUpPage").assertExists()
    }

    @Test
    fun testScreenFlowThroughGoogle(){
        // Starting from startUp page
        composeTestRule.onNodeWithTag("startUpScreen").assertExists()

        // Preform a click on google button
        composeTestRule.onNodeWithTag("googleButton").performClick()

        // Check if google page is displayed
        composeTestRule.onNodeWithTag("googleScreen").assertExists()

        // Click back button to go back the startUp page.
        composeTestRule.onNodeWithTag("goBackToStartUpScreen").performClick()
    }

    @Test
    fun testScreenFlowThroughMailLogin(){
        // Starting from startUp page
        composeTestRule.onNodeWithTag("startUpScreen").assertExists()

        // Preform a click on mail button
        composeTestRule.onNodeWithTag("mailButton").performClick()

        // Check if mail page is displayed
        composeTestRule.onNodeWithTag("mailPage").assertExists()

        // Preform a click on login button
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Check if login page is displayed
        composeTestRule.onNodeWithTag("loginScreen").assertExists()

        // Entering wrong information
        composeTestRule.onNodeWithTag("loginEmailField")
            .performTextInput("den.com")
        composeTestRule.onNodeWithTag("loginPasswordField")
            .performTextInput("")
        composeTestRule.onNodeWithTag("loginNextScreenButton").performClick()
        composeTestRule.onNodeWithTag("loginScreen").assertExists()

        // Entering correct information
        composeTestRule.onNodeWithTag("loginEmailField")
            .performTextInput("denis@gmail.com")
        composeTestRule.onNodeWithTag("loginPasswordField")
            .performTextInput("ILoveGod")
        composeTestRule.onNodeWithTag("loginNextScreenButton").performClick()
        composeTestRule.onNodeWithTag("HomeScreen").assertExists()
    }
}