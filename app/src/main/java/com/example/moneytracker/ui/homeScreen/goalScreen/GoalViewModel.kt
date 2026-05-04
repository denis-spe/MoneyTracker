// Grace come the LORD of hosts and to his CHRIST JESUS,
// who is the KING of kings and LORD of lords, who alone has immortality,
// who dwells in unapproachable light, whom no one has ever seen or can see.
// To him be honor and eternal dominion. Amen.
package com.example.moneytracker.ui.homeScreen.goalScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor() : ViewModel() {
    fun onGoalCardClick(goalId: String) {
        // Handle goal card click, e.g., navigate to goal details
    }
}