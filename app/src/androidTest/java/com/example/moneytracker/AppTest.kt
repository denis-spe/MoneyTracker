package com.example.moneytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(FirebaseAuthModule::class)
@RunWith(AndroidJUnit4::class)
class AppTest : TestBase(screenComposable = {
    App(
        homeViewModel = hiltViewModel(),
        showAllViewModel = hiltViewModel(),
        onFullyDrawn = {}
    )
}) {

    @Test
    fun testIfAppStartsOnUnloggedScreen() {
        val screenId = composeTestRule.activity
            .getString(R.string.startUpScreenId)
        composeTestRule.onNodeWithTag(screenId)
            .assertExists()
            .assertIsDisplayed()
    }
}