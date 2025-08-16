// Bless be the LORD ALMIGHTY

package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            LoginScreen()
        }
    }

    @Test
    fun testDisplayedContentInLoginScreen() {
        val loginScreenId = composeTestRule.activity
            .getString(R.string.loginScreenId)
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)

        val loginDescription = composeTestRule.activity
            .getString(R.string.loginDescriptionId)
        val loginDescriptionText = composeTestRule.activity
            .getString(R.string.login_desc_text)
        val loginImg = composeTestRule.activity
            .getString(R.string.login_img)

        val loginEmailField = composeTestRule.activity
            .getString(R.string.loginEmailFieldId)
        val loginEmailFieldPlaceholder = composeTestRule.activity
            .getString(R.string.loginEmailFieldPlaceholder)
        val emailOutlineFieldIcon = composeTestRule.activity
            .getString(R.string.emailOutlineIconId)

        val loginPasswordField = composeTestRule.activity
            .getString(R.string.loginPasswordFieldId)
        val loginPasswordPlaceholder = composeTestRule.activity
            .getString(R.string.loginPasswordPlaceholder)
        val passwordOutlineIcon = composeTestRule.activity
            .getString(R.string.passwordOutlineIconId)
        val loginButton = composeTestRule.activity
            .getString(R.string.loginBtnId)
        val loginPageFlowImg = composeTestRule.activity
            .getString(R.string.login_page_flow_img)
        val backButton = composeTestRule.activity
            .getString(R.string.authBackBtnId)


        composeTestRule.onNodeWithTag(loginScreenId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleMoney)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleTracker)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(loginDescription)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(loginDescriptionText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(loginImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(loginEmailField)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(loginEmailFieldPlaceholder)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(emailOutlineFieldIcon)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(loginPasswordField)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(loginPasswordPlaceholder)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(passwordOutlineIcon)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(loginButton)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(loginPageFlowImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(backButton)
            .assertExists()
            .assertIsDisplayed()
    }
}