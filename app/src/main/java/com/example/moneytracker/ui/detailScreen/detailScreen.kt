// Bless be the  names of LORD of hosts and of his JESUS CHRIST
package com.example.moneytracker.ui.detailScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.CountAchievement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.dataAddition.AddGoalAttained
import com.example.moneytracker.ui.homeScreen.dataAddition.DeleteAchievementButton
import com.example.moneytracker.ui.homeScreen.dataAddition.EditAchievementAmount
import com.example.moneytracker.ui.homeScreen.dataAddition.EditGoal
import com.example.moneytracker.ui.theme.StewardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    navController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val detailStates by viewModel.detailState.collectAsStateWithLifecycle()
    val financeEntity = detailStates.financeEntity
    val countAchievement = detailStates.countAchievement
    val attainColor = colorResource(R.color.Attain)
    val goalColor = colorResource(R.color.Goal)

    var selectedAchievement by remember {
        mutableStateOf<com.example.moneytracker.backend.storage.Achievement?>(
            null
        )
    }

    LaunchedEffect(goalId) {
        viewModel.loadGoal(goalId)
        viewModel.loadAchievementCounts(goalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Details") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.safePopBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    EditGoal(
                        color = goalColor.copy(0.3f),
                        goalId = goalId
                    )

                    AddGoalAttained(
                        color = attainColor.copy(0.3f),
                        goalId = goalId
                    )
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = financeEntity,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                        animationSpec = tween(
                            500
                        )
                    )
                },
                label = "GoalDetailContent"
            ) { state ->
                when (state) {
                    is DataState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is DataState.Success -> {
                        val currentGoal = state.data as? FinanceEntity.Goal
                        if (currentGoal != null) {
                            val countAchievementData = if (countAchievement is DataState.Success) {
                                countAchievement.data
                            } else {
                                null
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    GoalSummaryCard(
                                        currentGoal = currentGoal,
                                        countAchievement = countAchievementData
                                    )
                                }

                                if (currentGoal.achievement.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Achievement History",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    items(
                                        items = currentGoal.achievement,
                                        key = { it.achievementId.ifEmpty { it.startDateTime.toString() } }
                                    ) { achievement ->
                                        Box(modifier = Modifier.animateItem()) {
                                            AchievementItem(
                                                achievement = achievement,
                                                onClick = { selectedAchievement = achievement }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Goal not found")
                            }
                        }
                    }

                    is DataState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error: ${state.exception.message}")
                        }
                    }
                }
            }
        }
    }

    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { selectedAchievement = null }
        )
    }
}

@Composable
fun GoalSummaryCard(
    currentGoal: FinanceEntity.Goal,
    countAchievement: CountAchievement?
) {
    val totalSettled = currentGoal.settlement.sumOf { it.amount }
    val targetAmount = currentGoal.amount

    val animatedSettled = remember { Animatable(0f) }
    LaunchedEffect(totalSettled) {
        animatedSettled.animateTo(
            targetValue = totalSettled.toFloat(),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val currentAnimatedProgress =
        if (targetAmount > 0) (animatedSettled.value / targetAmount).toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Image(
                    painter = painterResource(currentGoal.tagIcon.icon),
                    contentDescription = currentGoal.tagIcon.name,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = currentGoal.label,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            if (currentGoal.description.isNotEmpty()) {
                Text(
                    text = currentGoal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            countAchievement?.let {
                val verticalDividerModifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 10.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            it.achievement.toString(),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Achieved")
                    }

                    VerticalDivider(modifier = verticalDividerModifier)

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            it.overdue.toString(),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Overdue")
                    }

                    VerticalDivider(modifier = verticalDividerModifier)

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            it.countAll.toString(),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Overall")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Target: ${currentGoal.amount.formatToAmount()}")
                Text(text = "Settled: ${animatedSettled.value.toDouble().formatToAmount()}")
            }
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { currentAnimatedProgress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorResource(id = R.color.success_complete),
                trackColor = colorResource(id = R.color.gray).copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(currentAnimatedProgress * 100).toInt()}% achieved",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AchievementItem(
    achievement: com.example.moneytracker.backend.storage.Achievement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.status,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.status == "COMPLETED") colorResource(id = R.color.success_complete) else colorResource(
                        id = R.color.error_color
                    )
                )
                Text(
                    text = achievement.totalSettlementAmount.formatToAmount(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Period: ${achievement.startDateTime.formatToDateTime} - ${achievement.deadlineDateTime.formatToDateTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AchievementDetailDialog(
    achievement: com.example.moneytracker.backend.storage.Achievement,
    onDismiss: () -> Unit
) {
    val status by remember {
        mutableStateOf(achievement.status)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Achievement Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        label = "Status",
                        value = status,
                        valueColor = if (status == "COMPLETED") colorResource(id = R.color.success_complete) else colorResource(
                            id = R.color.error_color
                        )
                    )
                    DetailRow(
                        label = "Amount",
                        value = achievement.totalSettlementAmount.formatToAmount()
                    )
                    DetailRow(label = "Start", value = achievement.startDateTime.formatToDateTime)
                    DetailRow(
                        label = "Deadline",
                        value = achievement.deadlineDateTime.formatToDateTime
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    EditAchievementAmount(
                        achievement = achievement
                    )

                    DeleteAchievementButton(achievement = achievement)
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        "Close",
                        color = StewardTheme.colors.onSurfaceText
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
