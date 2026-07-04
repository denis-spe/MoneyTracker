package com.example.moneytracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.homeScreen.HomeMainViewModel
import com.example.moneytracker.ui.homeScreen.allScreen.AllViewModel
import com.example.moneytracker.ui.homeScreen.overviewScreen.OverviewViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayViewModel
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayViewModel
import com.example.moneytracker.ui.screenManager.ScreenManager
import com.example.moneytracker.ui.showAll.ShowAllViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun App(
    showAllViewModel: ShowAllViewModel,
    onFullyDrawn: () -> Unit,
    homeMainViewModel: HomeMainViewModel,
    overviewViewModel: OverviewViewModel,
    todayViewModel: TodayViewModel,
    yesterdayViewModel: YesterdayViewModel,
    allViewModel: AllViewModel
) {
    ScreenManager(
        overviewViewModel = overviewViewModel,
        todayViewModel = todayViewModel,
        yesterdayViewModel = yesterdayViewModel,
        allViewModel = allViewModel,
        homeMainViewModel = homeMainViewModel,
        showAllViewModel = showAllViewModel,
        navController = rememberNavController(),
        onFullyDrawn = onFullyDrawn
    )
}