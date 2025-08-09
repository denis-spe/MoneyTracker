package com.example.moneytracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            MailScreen()
        }
    }

    @Test
    fun testContentsInMailScreen() {
        val mailScreenId = composeTestRule.activity
            .getString(R.string.mailScreenId)
        val titleMoney = composeTestRule.activity
            .getString(R.string.title_money)
        val titleTracker = composeTestRule.activity
            .getString(R.string.title_tracker)

        val mailDescription = composeTestRule.activity
            .getString(R.string.mailDescriptionId)
        val mailDescriptionText = composeTestRule.activity
            .getString(R.string.mail_description_text)
        val mailImg = composeTestRule.activity
            .getString(R.string.mail_img)
        val mailLoginBtn = composeTestRule.activity
            .getString(R.string.mailLoginBtnId)
        val orText = composeTestRule.activity
            .getString(R.string.or_text)
        val mailRegisterBtn = composeTestRule.activity
            .getString(R.string.mailRegisterBtnId)
        val mailRegisterText = composeTestRule.activity
            .getString(R.string.mail_register_text)
        val mailPageFlowImg = composeTestRule.activity
            .getString(R.string.mail_page_flow_img)

        composeTestRule.onNodeWithTag(mailScreenId)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleMoney)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(titleTracker)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(mailDescription)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(mailDescriptionText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(mailImg)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(mailLoginBtn)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(orText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(mailRegisterBtn)
            .assertExists()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(mailRegisterText)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(mailPageFlowImg)
            .assertExists()
            .assertIsDisplayed()
    }
}