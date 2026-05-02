// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind, and
// you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.helper.toMidnight
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.DottedDivider
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.dataAddition.DateTimeInput
import com.example.moneytracker.ui.homeScreen.dataAddition.DateTimeRange
import com.example.moneytracker.ui.homeScreen.dataAddition.FONT_WEIGHT
import com.example.moneytracker.ui.homeScreen.dataAddition.MAX_LABEL_LENGTH
import com.example.moneytracker.ui.homeScreen.dataAddition.MODEL_DRAWER_ICON_SIZE
import com.example.moneytracker.ui.homeScreen.dataAddition.MaxWidth
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerAmountField
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerButton
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerTag
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerTextField
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.toJavaLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.temporal.ChronoUnit

private val ICON_SIZE = 25.dp

@Composable
fun FinanceReceipt(
    financeEntity: FinanceEntity,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val datetime = financeEntity.createdAt.toLocalDateTimeUtc()
    val day = datetime.day.addZeroIfLessThenTen
    val month = datetime.month.name.title
    val year = datetime.year.addZeroIfLessThenTen
    val hour = datetime.hour.addZeroIfLessThenTen
    val minute = datetime.minute.addZeroIfLessThenTen
    val weekDay = datetime.dayOfWeek.name.title

    val time = "${hour}:${minute}"
    val date = "$day $month $year"

    val fontSize = 13.sp
    val color = colorResource(financeEntity.colorRes)
    val textDecoration = if (
        (financeEntity is FinanceEntity.Liability && financeEntity.remainingAmount == 0.0) || financeEntity.status == Status.OVERDUE
    )
        TextDecoration.LineThrough else
        TextDecoration.None
    val dataAdjust = DataAdjust.Data(financeEntity)

    Column(
        modifier = Modifier
            .fillMaxWidth(MaxWidth)
            .padding(top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = financeEntity.categoryText,
            fontSize = 25.sp,
            fontWeight = FONT_WEIGHT,
            color = color
        )
        Text(text = "On ${weekDay}, $date", fontSize = fontSize)
        Text(text = "At $time", fontSize = fontSize)

        DottedDivider(color = color, modifier = Modifier.padding(vertical = 10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val label = when (financeEntity) {
                is FinanceEntity.Transaction -> when (financeEntity.transactionType) {
                    TransactionType.EARNINGS -> "Received from:"
                    TransactionType.EXPENSES -> "Spent on:"
                    TransactionType.SAVINGS -> "Savings for:"
                }

                is FinanceEntity.Goal -> "Your goal:"
                is FinanceEntity.Liability -> when (financeEntity.liabilityType) {
                    LiabilityType.DEBT -> "Debt from:"
                    LiabilityType.LOAN -> "Lent to:"
                }
            }

            Text(text = label, fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(
                text = financeEntity.label,
                textDecoration = textDecoration,
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Amount:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(
                text = financeEntity.amount.formatToAmount(),
                textDecoration = textDecoration,
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Payment method:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(text = financeEntity.paymentMethod.text, fontSize = fontSize)
        }

        val adjustments = when (financeEntity) {
            is FinanceEntity.Goal -> financeEntity.adjustment
            is FinanceEntity.Liability -> financeEntity.adjustment
            is FinanceEntity.Transaction -> emptyList()
        }

        if (adjustments.isNotEmpty()) {
            val lastPayment = adjustments[adjustments.size - 1]
            val date = lastPayment.dateTime.toLocalDateTimeUtc()
            val day = date.day.addZeroIfLessThenTen
            val month = date.month.number.addZeroIfLessThenTen
            val year = date.year
            val hour = date.hour.addZeroIfLessThenTen
            val minute = date.minute.addZeroIfLessThenTen
            val time = "${hour}:${minute}"
            val dateString = "${day}/${month}/${year}"


            Text("")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Remaining Amount:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = financeEntity.remainingAmount.formatToAmount(), fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Payment:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = lastPayment.amount.formatToAmount(), fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Payment Date:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = dateString, fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Payment Time:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = time, fontSize = fontSize)
            }
        }

        if (financeEntity is FinanceEntity.Goal) {
            val deadlineDateTime = financeEntity.routine.deadlineDateTime.toLocalDateTimeUtc()
            val deadlineDay = deadlineDateTime.day.addZeroIfLessThenTen
            val deadlineMonth = deadlineDateTime.month.name.title.take(3)
            val deadlineYear = deadlineDateTime.year
            val deadlineHour = deadlineDateTime.hour.addZeroIfLessThenTen
            val deadlineMinute = deadlineDateTime.minute.addZeroIfLessThenTen

            val deadlineDate = "$deadlineDay $deadlineMonth $deadlineYear"
            val deadlineTime = "$deadlineHour:$deadlineMinute"

            val adjustStatue = remember { mutableStateOf(financeEntity.status) }

            LaunchedEffect(deadlineDateTime) {
                val now = LocalDateTime.now()
                val delayMillis = ChronoUnit.MILLIS.between(
                    now.toJavaLocalDateTime(),
                    deadlineDateTime.toJavaLocalDateTime()
                )

                if (delayMillis > 0) {
                    delay(delayMillis)
                    adjustStatue.value = financeEntity.status
                } else {
                    adjustStatue.value = financeEntity.status
                }
            }


            Text("")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Status:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                StatusView(
                    dataAdjust = dataAdjust,
                    showImageStatus = false,
                    fontSize = fontSize,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Deadline Date:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = deadlineDate, fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Deadline Time:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = deadlineTime, fontSize = fontSize)
            }
        }

        if (financeEntity.description.isNotBlank()) {
            DottedDivider(color = color, modifier = Modifier.padding(vertical = 10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Notes", fontSize = fontSize, fontWeight = FONT_WEIGHT)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        Text(
                            text = financeEntity.description,
                            fontSize = fontSize,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (financeEntity is FinanceEntity.Goal || financeEntity is FinanceEntity.Liability) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusView(
                    dataAdjust,
                    showImageStatus = true,
                    imageSize = ICON_SIZE
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onEdit,
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = color,
                )
            }

            IconButton(
                onClick = onDelete,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = color,
                )
            }

            IconButton(
                onClick = onClose,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = color
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                val icon = when (financeEntity) {
                    is FinanceEntity.Transaction -> when (financeEntity.transactionType) {
                        TransactionType.EARNINGS -> R.drawable.outline_earnings
                        TransactionType.EXPENSES -> R.drawable.filled_expenditure
                        TransactionType.SAVINGS -> R.drawable.outline_savings
                    }

                    is FinanceEntity.Goal -> R.drawable.outlined_goal
                    is FinanceEntity.Liability -> when (financeEntity.liabilityType) {
                        LiabilityType.DEBT -> R.drawable.filled_debt
                        LiabilityType.LOAN -> R.drawable.outline_lent
                    }
                }
                Icon(
                    painter = painterResource(icon),
                    contentDescription = financeEntity.categoryText,
                    tint = color,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }

            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Image(
                    painter = painterResource(financeEntity.tagIcon.icon),
                    contentDescription = financeEntity.categoryText,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }

            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Image(
                    painter = painterResource(financeEntity.paymentMethod.icon),
                    contentDescription = financeEntity.categoryText,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }
        }
    }
}

@Composable
fun AdjustmentReceipt(
    adjustment: Adjustment,
    financeEntity: FinanceEntity,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val datetime = adjustment.dateTime.toLocalDateTimeUtc()
    val day = datetime.day.addZeroIfLessThenTen
    val month = datetime.month.name.title
    val year = datetime.year.addZeroIfLessThenTen
    val hour = datetime.hour.addZeroIfLessThenTen
    val minute = datetime.minute.addZeroIfLessThenTen
    val weekDay = datetime.dayOfWeek.name.title

    val time = "${hour}:${minute}"
    val date = "$day $month $year"

    val fontSize = 13.sp

    val color = colorResource(adjustment.adjustmentType.color)

    val textDecoration = if (
        financeEntity.remainingAmount == 0.0
    )
        TextDecoration.LineThrough else
        TextDecoration.None
    val title = when (financeEntity) {
        is FinanceEntity.Liability -> if (financeEntity.liabilityType == LiabilityType.DEBT) "Repaid Debt" else "Repaid Loan"
        is FinanceEntity.Goal -> "Attained Goal"
        else -> "Adjustment"
    }
    val dataAdjust = DataAdjust.Adjust(adjustment)


    Column(
        modifier = Modifier
            .fillMaxWidth(MaxWidth)
            .padding(top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FONT_WEIGHT,
            color = color
        )
        Text(text = "On ${weekDay}, $date", fontSize = fontSize)
        Text(text = "At $time", fontSize = fontSize)

        DottedDivider(color = color, modifier = Modifier.padding(vertical = 10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val label = when (adjustment.adjustmentType) {
                AdjustmentType.LENT_REPAY -> "Lent Repayment:"
                AdjustmentType.DEBT_REPAY -> "Debt Repayment:"
                AdjustmentType.GOAL_ATTAIN -> "Attained:"
                else -> "Repayment:"
            }

            Text(text = label, fontSize = fontSize, fontWeight = FONT_WEIGHT)
            val labelState = remember { mutableStateOf("") }
            labelState.value = financeEntity.label
            Text(
                text = labelState.value,
                textDecoration = textDecoration,
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Amount:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(
                text = adjustment.amount.formatToAmount(),
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Payment method:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(text = adjustment.paymentMethod.text, fontSize = fontSize)
        }

        val adjustments = when (financeEntity) {
            is FinanceEntity.Goal -> financeEntity.adjustment
            is FinanceEntity.Liability -> financeEntity.adjustment
            is FinanceEntity.Transaction -> emptyList()
        }

        if (adjustments.isNotEmpty()) {
            val lastPayment = adjustments[adjustments.size - 1]
            val date = lastPayment.dateTime.toLocalDateTimeUtc()
            val day = date.day.addZeroIfLessThenTen
            val month = date.month.number.addZeroIfLessThenTen
            val year = date.year
            val hour = date.hour.addZeroIfLessThenTen
            val minute = date.minute.addZeroIfLessThenTen
            val time = "${hour}:${minute}"
            val dateString = "${day}/${month}/${year}"


            Text("")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Remaining Amount:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = financeEntity.remainingAmount.formatToAmount(), fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Payment:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = lastPayment.amount.formatToAmount(), fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Payment Date:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = dateString, fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Payment Time:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = time, fontSize = fontSize)
            }
        }

        if (adjustment.description.isNotBlank()) {
            DottedDivider(color = color, modifier = Modifier.padding(vertical = 10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Notes", fontSize = fontSize, fontWeight = FONT_WEIGHT)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        Text(
                            text = adjustment.description,
                            fontSize = fontSize,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (financeEntity is FinanceEntity.Goal || financeEntity is FinanceEntity.Liability) {
            if (financeEntity.remainingAmount == 0.0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusView(
                        dataAdjust,
                        showImageStatus = true,
                        imageSize = ICON_SIZE
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onEdit,
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = color,
                )
            }

            IconButton(
                onClick = onDelete,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = color,
                )
            }

            IconButton(
                onClick = onClose,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = color
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    painter = painterResource(adjustment.adjustmentType.icon),
                    contentDescription = adjustment.adjustmentType.text,
                    tint = color,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }

            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Image(
                    painter = painterResource(adjustment.tagIcon.icon),
                    contentDescription = adjustment.adjustmentType.text,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }

            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Image(
                    painter = painterResource(adjustment.paymentMethod.icon),
                    contentDescription = adjustment.paymentMethod.text,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }
        }
    }
}

@Composable
fun OnDeleteReceipt(
    dataAdjust: DataAdjust,
    onShowDeleteDialog: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    val item = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.categoryText
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.text
    }

    if (onShowDeleteDialog.value) {
        Dialog(
            onDismissRequest = {
                onShowDeleteDialog.value = false
            }
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp, end = 3.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Are you sure you want to delete this ${item.lowercase()} item",
                            textAlign = TextAlign.Center
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(top = 10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                onShowDeleteDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray.copy(0.2f)
                            )
                        ) {
                            Text("Cancel", color = MoneyTrackerTheme.colors.autoText)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.error_color)
                            )
                        ) {
                            Text("Delete", color = MoneyTrackerTheme.colors.autoText)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnUpdate(
    dataAdjust: DataAdjust,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    isUpdateModelBottonOpen: MutableState<Boolean>,
    onShowDialog: MutableState<Boolean>
) {
    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val endLocalDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val displayAmountState = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val displayLabel = rememberSaveable { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val wasAdjustmentSuccess = remember { mutableStateOf(State.INITIAL) }
    val tagIconState = remember { mutableStateOf(TagIcon("description", R.drawable.description)) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val lazyState = rememberLazyListState()

    val dataTypeText = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.categoryText
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.text
    }

    val colorResId = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.colorRes
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.color
    }

    val icon = when (dataAdjust) {
        is DataAdjust.Data -> when (val f = dataAdjust.financeEntity) {
            is FinanceEntity.Transaction -> when (f.transactionType) {
                TransactionType.EARNINGS -> R.drawable.filled_earnings
                TransactionType.EXPENSES -> R.drawable.filled_expenditure
                TransactionType.SAVINGS -> R.drawable.filled_savings
            }

            is FinanceEntity.Goal -> R.drawable.filled_goal
            is FinanceEntity.Liability -> when (f.liabilityType) {
                LiabilityType.DEBT -> R.drawable.filled_debt
                LiabilityType.LOAN -> R.drawable.filled_lent
            }
        }

        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.icon
    }

    val description = when (dataAdjust) {
        is DataAdjust.Data -> "Updating ${dataAdjust.financeEntity.label} " +
                dataAdjust.financeEntity.categoryText

        is DataAdjust.Adjust -> "Updating ${dataAdjust.adjustment.financeEntity?.label} " +
                dataAdjust.adjustment.adjustmentType.text
    }

    val color = colorResource(colorResId)

    LaunchedEffect(dataAdjust, isUpdateModelBottonOpen.value) {
        if (isUpdateModelBottonOpen.value) {
            when (dataAdjust) {
                is DataAdjust.Data -> {
                    val finance = dataAdjust.financeEntity
                    amountState.setTextAndPlaceCursorAtEnd(finance.amount.toString())
                    labelState.setTextAndPlaceCursorAtEnd(finance.label)
                    descriptionState.setTextAndPlaceCursorAtEnd(finance.description)
                    localDateTimeState.value = if (finance is FinanceEntity.Goal) {
                        finance.createdAt.toLocalDateTimeUtc().toMidnight()
                    } else {
                        finance.createdAt.toLocalDateTimeUtc()
                    }
                    endLocalDateTimeState.value = if (finance is FinanceEntity.Goal) {
                        finance.routine.deadlineDateTime.toLocalDateTimeUtc().toMidnight()
                    } else {
                        finance.createdAt.toLocalDateTimeUtc()
                    }
                    tagIconState.value = finance.tagIcon
                    selectedPaymentMethod.value = finance.paymentMethod
                }

                is DataAdjust.Adjust -> {
                    val adjustment = dataAdjust.adjustment
                    amountState.setTextAndPlaceCursorAtEnd(adjustment.amount.toString())
                    labelState.setTextAndPlaceCursorAtEnd(adjustment.label)
                    descriptionState.setTextAndPlaceCursorAtEnd(adjustment.description)
                    localDateTimeState.value = adjustment.dateTime.toLocalDateTimeUtc()
                    tagIconState.value = adjustment.tagIcon
                    selectedPaymentMethod.value = adjustment.paymentMethod
                }
            }
        }
    }

    if (isUpdateModelBottonOpen.value) {
        ModalBottomSheet(
            onDismissRequest = {
                isUpdateModelBottonOpen.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val color = colorResource(id = colorResId)

                    Icon(
                        modifier = Modifier
                            .size(MODEL_DRAWER_ICON_SIZE)
                            .padding(end = 5.dp),
                        painter = painterResource(id = icon),
                        contentDescription = dataTypeText,
                        tint = color
                    )

                    Text(description, color = color, fontWeight = FONT_WEIGHT)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // Amount
                    item(key = 322) {
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            ModelDrawerAmountField(
                                state = amountState,
                                placeholder = "0.0",
                                colorResId = colorResId,
                                showInRow = true,
                                wasSuccess = wasSuccess,
                                displayState = displayAmountState
                            )
                        }
                    }

                    // Label
                    item(key = 171) {
                        if (dataAdjust is DataAdjust.Data) {
                            Row(
                                modifier = Modifier.animateItem()
                            ) {
                                ModelDrawerTextField(
                                    state = labelState,
                                    title = "Label",
                                    description = "Add a label for the given amount",
                                    placeholder = dataAdjust.financeEntity.label,
                                    colorResId = colorResId,
                                    wasSuccess = wasSuccess,
                                    textLength = MAX_LABEL_LENGTH,
                                    displayText = displayLabel
                                )
                            }
                        }
                    }

                    // Tag
                    item(key = 58) {
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            ModelDrawerTag(
                                colorResId = colorResId,
                                title = "Tag",
                                iconState = tagIconState
                            )
                        }
                    }

                    // Description
                    item(key = 45) {
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            ModelDrawerTextField(
                                state = descriptionState,
                                placeholder = "Note (Optional)",
                                title = "Note",
                                description = "Take a note for the given amount",
                                colorResId = colorResId,
                                wasSuccess = null,
                                displayText = rememberSaveable { mutableStateOf("") }
                            )
                        }
                    }

                    // Date time picker
                    item(key = 12) {
                        // Show date time picker.
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            if (
                                dataAdjust is DataAdjust.Data &&
                                dataAdjust.financeEntity is FinanceEntity.Goal
                            ) {
                                DateTimeRange(
                                    startLocalDateTimeState = localDateTimeState,
                                    endLocalDateTimeState = endLocalDateTimeState,
                                    colorResId = colorResId
                                )
                            } else {
                                DateTimeInput(
                                    showTime = showTime,
                                    showDate = showDate,
                                    localDateTimeState = localDateTimeState,
                                    colorResId = colorResId
                                )
                            }
                        }
                    }


                    // Updating dataset
                    item(key = 6) {
                        val amountAsDouble = amountState.text.toString().toDoubleOrNull()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (dataAdjust) {
                                is DataAdjust.Data -> {
                                    ModelDrawerButton(
                                        text = "Apply changes",
                                        wasSuccess = wasSuccess,
                                        colorResId = colorResId,
                                        filledColor = colorResource(colorResId),
                                        textColor = Color.White
                                    ) {
                                        if (amountAsDouble != null && labelState.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            val finance = dataAdjust.financeEntity
                                            val normalizedStart =
                                                localDateTimeState.value.toMidnight()
                                            val normalizedEnd =
                                                endLocalDateTimeState.value.toMidnight()

                                            val newFinanceEntity = when (finance) {
                                                is FinanceEntity.Transaction -> finance.copy(
                                                    amount = amountAsDouble,
                                                    label = labelState.text.toString(),
                                                    description = descriptionState.text.toString(),
                                                    createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                                    tagIcon = tagIconState.value,
                                                    paymentMethod = selectedPaymentMethod.value
                                                )

                                                is FinanceEntity.Goal -> finance.copy(
                                                    amount = amountAsDouble,
                                                    label = labelState.text.toString(),
                                                    description = descriptionState.text.toString(),
                                                    createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                                    tagIcon = tagIconState.value,
                                                    paymentMethod = selectedPaymentMethod.value,
                                                    routine = finance.routine.copy(
                                                        startDateTime = normalizedStart.toFirestoreTimestampUtc(),
                                                        deadlineDateTime = normalizedEnd.toFirestoreTimestampUtc()
                                                    )
                                                )

                                                is FinanceEntity.Liability -> finance.copy(
                                                    amount = amountAsDouble,
                                                    label = labelState.text.toString(),
                                                    description = descriptionState.text.toString(),
                                                    createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                                    tagIcon = tagIconState.value,
                                                    paymentMethod = selectedPaymentMethod.value
                                                )
                                            }

                                            viewModel.updateData(
                                                finance,
                                                newFinanceEntity
                                            )

                                            if (newFinanceEntity is FinanceEntity.Goal) {
                                                viewModel.beginTheWork(newFinanceEntity)
                                            }


                                            wasSuccess.value = State.SUCCESS
                                            userViewModel.showActionNotification(
                                                "Data updated successfully",
                                                color
                                            )

                                            // Reset all state
                                            amountState.clearText()
                                            labelState.clearText()
                                            descriptionState.clearText()
                                            tagIconState.value = TagIcon(
                                                "description",
                                                R.drawable.description
                                            )

                                            isUpdateModelBottonOpen.value = false
                                            onShowDialog.value = false

                                        } else {
                                            wasSuccess.value = State.ERROR
                                        }
                                    }
                                }

                                is DataAdjust.Adjust -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ModelDrawerButton(
                                            text = "Apply changes",
                                            wasSuccess = wasAdjustmentSuccess,
                                            colorResId = colorResId,
                                            filledColor = Color.Transparent
                                        ) {
                                            if (amountAsDouble != null
                                                && amountAsDouble
                                                <= dataAdjust.adjustment.financeEntity!!.remainingAmount
                                            ) {
                                                val adjustment = dataAdjust.adjustment
                                                val financeEntityType =
                                                    when (adjustment.financeEntity!!) {
                                                        is FinanceEntity.Transaction -> "TRANSACTION"
                                                        is FinanceEntity.Goal -> "GOAL"
                                                        is FinanceEntity.Liability -> "LIABILITY"
                                                    }
                                                viewModel.updateAdjustmentData(
                                                    adjustment.financeEntity!!.id,
                                                    financeEntityType,
                                                    adjustment,
                                                    Adjustment(
                                                        adjustmentId = adjustment.adjustmentId,
                                                        amount = amountAsDouble,
                                                        label = adjustment.label,
                                                        description = descriptionState.text.toString(),
                                                        dateTime = localDateTimeState
                                                            .value.toFirestoreTimestampUtc(),
                                                        tagIcon = tagIconState.value,
                                                        adjustmentType = adjustment.adjustmentType,
                                                        paymentMethod = adjustment.paymentMethod
                                                    )
                                                )


                                                wasSuccess.value = State.SUCCESS
                                                userViewModel.showActionNotification(
                                                    "Adjustment updated successfully",
                                                    color
                                                )

                                                // Reset all state
                                                amountState.clearText()
                                                labelState.clearText()
                                                descriptionState.clearText()
                                                tagIconState.value = TagIcon(
                                                    "description",
                                                    R.drawable.description
                                                )

                                                isUpdateModelBottonOpen.value = false
                                                onShowDialog.value = false

                                            } else {
                                                wasSuccess.value = State.ERROR
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
    }


    LaunchedEffect(isUpdateModelBottonOpen.value) {
        if (!isUpdateModelBottonOpen.value) return@LaunchedEffect

        // wait for BottomSheet to render first frame
        awaitFrame()

        val amount = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.financeEntity.amount
            is DataAdjust.Adjust -> dataAdjust.adjustment.amount
        }
        val label = if (dataAdjust is DataAdjust.Data) {
            dataAdjust.financeEntity.label
        } else ""

        val description = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.financeEntity.description
            is DataAdjust.Adjust -> dataAdjust.adjustment.description
        }

        val dateTime = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.financeEntity.createdAt
            is DataAdjust.Adjust -> dataAdjust.adjustment.dateTime
        }

        val tagIcon = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.financeEntity.tagIcon
            is DataAdjust.Adjust -> dataAdjust.adjustment.tagIcon
        }

        if (amountState.text.toString() != amount.toString()) {
            amountState.setTextAndPlaceCursorAtEnd(amount.toString())
            displayAmountState.value = amount.toString()

            labelState.setTextAndPlaceCursorAtEnd(label)
            displayLabel.value = label

            descriptionState.setTextAndPlaceCursorAtEnd(description)

            localDateTimeState.value = dateTime.toLocalDateTimeUtc()

            tagIconState.value = tagIcon
        }
    }

}

@Composable
fun Receipt(
    dataAdjust: DataAdjust,
    onShowDialog: MutableState<Boolean>
) {
    val onShowDeleteDialog = remember { mutableStateOf(false) }
    val isUpdateModelBottonOpen = remember { mutableStateOf(false) }
    val viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
    val userViewModel: UserViewModel = hiltViewModel<UserViewModel>()

    Dialog(
        onDismissRequest = {
            onShowDialog.value = false
        }
    ) {
        Card {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (dataAdjust) {
                    is DataAdjust.Data ->
                        FinanceReceipt(
                            financeEntity = dataAdjust.financeEntity,
                            onEdit = {
                                isUpdateModelBottonOpen.value = true
                            },
                            onDelete = {
                                onShowDeleteDialog.value = true
                            }
                        ) {
                            onShowDialog.value = false
                        }

                    is DataAdjust.Adjust -> {
                        AdjustmentReceipt(
                            adjustment = dataAdjust.adjustment,
                            financeEntity = dataAdjust.adjustment.financeEntity!!,
                            onEdit = {
                                isUpdateModelBottonOpen.value = true
                            },
                            onDelete = {
                                onShowDeleteDialog.value = true
                            }
                        ) {
                            onShowDialog.value = false
                        }
                    }
                }

            }
        }
    }

    OnDeleteReceipt(
        dataAdjust = dataAdjust,
        onShowDeleteDialog = onShowDeleteDialog,
    ) {
        when (dataAdjust) {
            is DataAdjust.Data -> {
                viewModel.removeData(dataAdjust.financeEntity)
                userViewModel.showActionNotification("Data deleted successfully", Color.Red)
            }

            is DataAdjust.Adjust -> {
                val financeEntityType = when (dataAdjust.adjustment.financeEntity!!) {
                    is FinanceEntity.Transaction -> "TRANSACTION"
                    is FinanceEntity.Goal -> "GOAL"
                    is FinanceEntity.Liability -> "LIABILITY"
                }
                viewModel.removeAdjustmentFinance(
                    dataAdjust.adjustment.financeEntity!!.id,
                    financeEntityType,
                    dataAdjust.adjustment
                )
                userViewModel.showActionNotification("Adjustment deleted successfully", Color.Red)
            }
        }
        onShowDialog.value = false
    }

    OnUpdate(
        dataAdjust = dataAdjust,
        viewModel = viewModel,
        userViewModel = userViewModel,
        isUpdateModelBottonOpen = isUpdateModelBottonOpen,
        onShowDialog = onShowDialog
    )
}
