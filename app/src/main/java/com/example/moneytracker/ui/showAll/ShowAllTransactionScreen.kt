// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.showAll

import androidx.compose.animation.animateBounds
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.limitLength
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.ui.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.screenManager.TransactionDetailScreenRouter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAllTransactionScreen(
    viewModel: ShowAllViewModel,
    navController: NavHostController
) {
    // Load all transactions once
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadAllTransaction()
    }

    // Collect the state from the ViewModel
    val showAllStates = viewModel.showAllDataset.collectAsStateWithLifecycle()

    // Extract the transactions from the state
    val transactions = showAllStates.value.transaction

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = { navController.safePopBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (transactions) {
            is DataState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DataState.Success -> {
                val data = transactions.data

                LookaheadScope {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(data.size, key = { data[it].id }) { index ->
                            ShowAllTransactionCard(
                                modifier = Modifier
                                    .animateItem()
                                    .animateBounds(this@LookaheadScope),
                                financeEntity = data[index],
                                onNavigate = navController
                            )
                        }
                    }
                }
            }

            is DataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${transactions.exception.message}")
                }
            }
        }
    }
}

@Composable
internal fun ShowAllTransactionCard(
    modifier: Modifier = Modifier,
    financeEntity: FinanceEntity.Transaction,
    onNavigate: NavController?
) {
    val amount = financeEntity.amount.formatToAmount()
    val label = financeEntity.label
    val tagIcon = painterResource(financeEntity.tagIcon.icon)
    val transactionType = financeEntity.transactionType.text
    val color = colorResource(financeEntity.colorRes)
    val paymentMethod = financeEntity.paymentMethod
    val dateTime = financeEntity.createdAt
    val description = financeEntity.description

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onNavigate?.navigate(
                    TransactionDetailScreenRouter(financeEntity.id)
                )
            },

        overlineContent = {
            Text(
                dateTime.formatToDateTime,
                style = typography.bodySmall.copy(fontSize = 12.sp),
                color = Color.Gray,
            )
        },
        headlineContent = {
            Text(
                label,
                style = typography.titleSmall,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        },

        leadingContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = tagIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .clip(RoundedCornerShape(15.dp))
                )

                Image(
                    painter = painterResource(paymentMethod.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .clip(RoundedCornerShape(15.dp))
                )
            }
        },

        supportingContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    transactionType,
                    style = typography.labelSmall,
                    color = color
                )

                if (description.isNotEmpty()) {
                    Text(
                        description.limitLength(250),
                        style = typography.bodySmall.copy(fontSize = 13.sp),
                        color = Color.Gray,
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        },
        trailingContent = {
            Text(
                amount,
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    )
}
