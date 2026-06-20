// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind, and
// you shall love your neighbor as yourself
package com.example.moneytracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.DeleteDialog
import com.example.moneytracker.ui.components.DottedDivider
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.dataAddition.AffectCurrentAccount
import com.example.moneytracker.ui.dataAddition.DateTimeInput
import com.example.moneytracker.ui.dataAddition.FONT_WEIGHT
import com.example.moneytracker.ui.dataAddition.MAX_LABEL_LENGTH
import com.example.moneytracker.ui.dataAddition.MaxWidth
import com.example.moneytracker.ui.dataAddition.ModelDrawerAmountField
import com.example.moneytracker.ui.dataAddition.ModelDrawerButton
import com.example.moneytracker.ui.dataAddition.ModelDrawerDescriptionTextField
import com.example.moneytracker.ui.dataAddition.ModelDrawerLabelTextField
import com.example.moneytracker.ui.dataAddition.ModelDrawerTag
import com.example.moneytracker.ui.dataAddition.PaymentMethodDropdown
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.toJavaLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.milliseconds

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
    val dataSettlement = DataSettlement.SettlementData(financeEntity)

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

        val settlements = when (financeEntity) {
            is FinanceEntity.Goal -> financeEntity.settlement
            is FinanceEntity.Liability -> financeEntity.settlement
            is FinanceEntity.Transaction -> emptyList()
        }

        if (settlements.isNotEmpty()) {
            val lastPayment = settlements[settlements.size - 1]
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
                    delay(delayMillis.milliseconds)
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
                    dataSettlement = dataSettlement,
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

        if (financeEntity is FinanceEntity.Transaction && financeEntity.withdrawal.isNotEmpty()) {
            val withdrawals = financeEntity.withdrawal
            val lastPayment = withdrawals[withdrawals.size - 1]
            val date = lastPayment.createdAt.toLocalDateTimeUtc()
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
                Text(text = "Last Withdrawn amount:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = lastPayment.amount.formatToAmount(), fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Withdrawn Date:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = dateString, fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Withdrawn Time:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = time, fontSize = fontSize)
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
                    dataSettlement,
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
fun SettlementReceipt(
    settlement: Settlement,
    financeEntity: FinanceEntity,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val datetime = settlement.dateTime.toLocalDateTimeUtc()
    val day = datetime.day.addZeroIfLessThenTen
    val month = datetime.month.name.title
    val year = datetime.year.addZeroIfLessThenTen
    val hour = datetime.hour.addZeroIfLessThenTen
    val minute = datetime.minute.addZeroIfLessThenTen
    val weekDay = datetime.dayOfWeek.name.title

    val time = "${hour}:${minute}"
    val date = "$day $month $year"

    val fontSize = 13.sp

    val color = colorResource(settlement.settlementType.color)

    val textDecoration = if (
        financeEntity.remainingAmount == 0.0
    )
        TextDecoration.LineThrough else
        TextDecoration.None
    val title = when (financeEntity) {
        is FinanceEntity.Liability -> if (financeEntity.liabilityType == LiabilityType.DEBT) "Repaid Debt" else "Repaid Loan"
        is FinanceEntity.Goal -> "Attained Goal"
        else -> "Settlement"
    }
    val dataSettlement = DataSettlement.SettlementAdjust(settlement)


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
            val label = when (settlement.settlementType) {
                SettlementType.LENT_REPAY -> "Lent Repayment:"
                SettlementType.DEBT_REPAY -> "Debt Repayment:"
                SettlementType.GOAL_ATTAIN -> "Attained:"
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
                text = settlement.amount.formatToAmount(),
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Payment method:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(text = settlement.paymentMethod.text, fontSize = fontSize)
        }

        val settlements = when (financeEntity) {
            is FinanceEntity.Goal -> financeEntity.settlement
            is FinanceEntity.Liability -> financeEntity.settlement
            is FinanceEntity.Transaction -> emptyList()
        }

        if (settlements.isNotEmpty()) {
            val lastPayment = settlements[settlements.size - 1]
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

        if (settlement.description.isNotBlank()) {
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
                            text = settlement.description,
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
                        dataSettlement,
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
                    painter = painterResource(settlement.settlementType.icon),
                    contentDescription = settlement.settlementType.text,
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
                    painter = painterResource(settlement.tagIcon.icon),
                    contentDescription = settlement.settlementType.text,
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
                    painter = painterResource(settlement.paymentMethod.icon),
                    contentDescription = settlement.paymentMethod.text,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }
        }
    }
}


@Composable
fun WithdrawalReceipt(
    withdrawal: Withdrawal,
    financeEntity: FinanceEntity,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val datetime = withdrawal.createdAt.toLocalDateTimeUtc()
    val day = datetime.day.addZeroIfLessThenTen
    val month = datetime.month.name.title
    val year = datetime.year.addZeroIfLessThenTen
    val hour = datetime.hour.addZeroIfLessThenTen
    val minute = datetime.minute.addZeroIfLessThenTen
    val weekDay = datetime.dayOfWeek.name.title

    val time = "${hour}:${minute}"
    val date = "$day $month $year"

    val fontSize = 13.sp

    val color = colorResource(SettlementType.WITHDRAWAL.color)

    val textDecoration = if (
        financeEntity.remainingAmount == 0.0
    )
        TextDecoration.LineThrough else
        TextDecoration.None
    val title = "Withdrawal"
    val dataSettlement = DataSettlement.SettlementWithdrawal(withdrawal)


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
            val label = "Withdrawn from: "

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
                text = withdrawal.amount.formatToAmount(),
                fontSize = fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "To payment method:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(text = withdrawal.toPaymentMethod.text, fontSize = fontSize)
        }

        val settlements = if (financeEntity is FinanceEntity.Transaction) {
            financeEntity.withdrawal
        } else emptyList()

        if (settlements.isNotEmpty()) {
            val lastPayment = settlements[settlements.size - 1]
            val date = lastPayment.createdAt.toLocalDateTimeUtc()
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
                Text(text = "Last Withdrawn amount:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = lastPayment.amount.formatToAmount(), fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Withdrawn Date:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = dateString, fontSize = fontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Last Withdrawn Time:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = time, fontSize = fontSize)
            }
        }

        if (withdrawal.description.isNotBlank()) {
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
                            text = withdrawal.description,
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
                        dataSettlement,
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
                Image(
                    painter = painterResource(financeEntity.tagIcon.icon),
                    contentDescription = SettlementType.WITHDRAWAL.text,
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
                    painter = painterResource(withdrawal.toPaymentMethod.icon),
                    contentDescription = withdrawal.toPaymentMethod.text,
                    modifier = Modifier
                        .size(ICON_SIZE)
                )
            }
        }
    }
}

@Composable
fun OnDeleteReceipt(
    dataSettlement: DataSettlement,
    onShowDeleteDialog: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    DeleteDialog(
        showDialog = onShowDeleteDialog,
        title = "Delete ${dataSettlement.text}?",
        paragraph = "Are you sure you want to delete this ${dataSettlement.text.lowercase()} item? This action cannot be undone.",
        onConfirm = onConfirm
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnUpdate(
    dataSettlement: DataSettlement,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    isUpdateModelBottonOpen: MutableState<Boolean>,
    onShowDialog: MutableState<Boolean>
) {
    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val onCreatedDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val displayAmountState = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val displayLabel = rememberSaveable { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val wasSettlementSuccess = remember { mutableStateOf(State.INITIAL) }
    val tagIconState = remember { mutableStateOf(TagIcon("description", R.drawable.description)) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val toPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val fromPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val lazyState = rememberLazyListState()
    val affectCurrentAccount =
        rememberSaveable { mutableStateOf(dataSettlement.affectCurrentAccount) }

    val dataTypeText = dataSettlement.text
    val colorResId = dataSettlement.colorRes
    val icon = dataSettlement.icon



    val description = when (dataSettlement) {
        is DataSettlement.SettlementData -> "Update ${dataSettlement.financeEntity.label} " +
                dataSettlement.text

        is DataSettlement.SettlementAdjust -> "Update ${dataSettlement.settlement.financeEntity?.label} " +
                dataSettlement.text

        is DataSettlement.SettlementWithdrawal -> "Update ${dataSettlement.withdrawal.financeEntity?.label} " +
                dataSettlement.text
    }

    val color = colorResource(colorResId)
    val topModifier = Modifier.clip(
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
    )

    LaunchedEffect(dataSettlement, isUpdateModelBottonOpen.value) {
        if (isUpdateModelBottonOpen.value) {
            // wait for BottomSheet to render first frame
            awaitFrame()

            when (dataSettlement) {
                is DataSettlement.SettlementData -> {
                    val finance = dataSettlement.financeEntity
                    amountState.setTextAndPlaceCursorAtEnd(finance.amount.toString())
                    displayAmountState.value = finance.amount.toString()
                    labelState.setTextAndPlaceCursorAtEnd(finance.label)
                    displayLabel.value = finance.label
                    descriptionState.setTextAndPlaceCursorAtEnd(finance.description)
                    onCreatedDateTimeState.value = finance.createdAt.toLocalDateTimeUtc()

                    tagIconState.value = finance.tagIcon
                    selectedPaymentMethod.value = finance.paymentMethod
                    affectCurrentAccount.value =
                        finance is FinanceEntity.Transaction && finance.affectCurrentAccount ||
                                finance is FinanceEntity.Liability && finance.affectCurrentAccount
                }

                is DataSettlement.SettlementAdjust -> {
                    val settlement = dataSettlement.settlement
                    amountState.setTextAndPlaceCursorAtEnd(settlement.amount.toString())
                    displayAmountState.value = settlement.amount.toString()
                    labelState.setTextAndPlaceCursorAtEnd(settlement.label)
                    displayLabel.value = settlement.label
                    descriptionState.setTextAndPlaceCursorAtEnd(settlement.description)
                    onCreatedDateTimeState.value = settlement.dateTime.toLocalDateTimeUtc()
                    tagIconState.value = settlement.tagIcon
                    selectedPaymentMethod.value = settlement.paymentMethod
                    affectCurrentAccount.value = settlement.affectCurrentAccount
                }

                is DataSettlement.SettlementWithdrawal -> {
                    val withdrawal = dataSettlement.withdrawal
                    amountState.setTextAndPlaceCursorAtEnd(withdrawal.amount.toString())
                    displayAmountState.value = withdrawal.amount.toString()
                    labelState.setTextAndPlaceCursorAtEnd(withdrawal.label)
                    displayLabel.value = withdrawal.label
                    descriptionState.setTextAndPlaceCursorAtEnd(withdrawal.description)
                    onCreatedDateTimeState.value = withdrawal.createdAt.toLocalDateTimeUtc()
                    fromPaymentMethod.value = withdrawal.fromPaymentMethod
                    toPaymentMethod.value = withdrawal.toPaymentMethod
                    affectCurrentAccount.value = withdrawal.affectCurrentAccount
                }
            }
        }
    }

    val showAffectCurrentAccount =
        !(dataSettlement is DataSettlement.SettlementData && dataSettlement.financeEntityType == "GOAL") &&
                !(dataSettlement is DataSettlement.SettlementAdjust && dataSettlement.financeEntityType == "GOAL")

    if (isUpdateModelBottonOpen.value) {
        ModalBottomSheet(
            onDismissRequest = {
                isUpdateModelBottonOpen.value = false
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val color = colorResource(id = colorResId)

                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 5.dp),
                        painter = painterResource(id = icon),
                        contentDescription = dataTypeText,
                        colorFilter = ColorFilter.tint(color)
                    )

                    Text(
                        text = dataTypeText,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )

                    Text(description, color = Color.Gray, fontSize = 12.sp)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = lazyState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // Amount
                    item(key = 322) {
                        Row(
                            modifier = Modifier
                                .animateItem()
                        ) {
                            if (dataSettlement.financeEntityType != "GOAL") {
                                ModelDrawerAmountField(
                                    modifier = topModifier,
                                    state = amountState,
                                    placeholder = "0.0",
                                    colorResId = colorResId,
                                    wasSuccess = wasSuccess,
                                    displayState = displayAmountState,
                                )
                            }
                        }
                    }

                    // Label
                    item(key = 171) {
                        if (dataSettlement is DataSettlement.SettlementData) {
                            Row(
                                modifier = Modifier.animateItem()
                            ) {
                                ModelDrawerLabelTextField(
                                    state = labelState,
                                    title = "Label",
                                    description = "Add a label for the given amount",
                                    placeholder = dataSettlement.financeEntity.label,
                                    colorResId = colorResId,
                                    wasSuccess = wasSuccess,
                                    textLength = MAX_LABEL_LENGTH,
                                    displayText = displayLabel,
                                    modifier = if (dataSettlement.financeEntityType == "GOAL")
                                        Modifier else topModifier
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
                            ModelDrawerDescriptionTextField(
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
                        Column(
                            modifier = Modifier.animateItem()
                        ) {
                            DateTimeInput(
                                showTime = showTime,
                                showDate = showDate,
                                localDateTimeState = onCreatedDateTimeState,
                                colorResId = colorResId,
                                timeContainerModifier = if (dataSettlement.financeEntityType == "GOAL") Modifier else bottomModifier,
                            )
                        }
                    }

                    if (showAffectCurrentAccount) {
                        item(6544) {
                            // Show affectCurrentAccount.
                            Column(
                                modifier = Modifier.animateItem()
                            ) {
                                AffectCurrentAccount(
                                    label = "Affect current account",
                                    color = colorResource(colorResId),
                                    affectCurrentAccountState = affectCurrentAccount,
                                    containerModifier = bottomModifier
                                )
                            }
                        }
                    }

                    // Payment Methods for Withdrawal
                    if (dataSettlement is DataSettlement.SettlementWithdrawal) {
                        item {
                            Text(
                                "From account:",
                                color = color,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            PaymentMethodDropdown(
                                colorResId = colorResId,
                                selectedPaymentMethod = fromPaymentMethod
                            )
                            Text(
                                "To account:",
                                color = color,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            PaymentMethodDropdown(
                                colorResId = colorResId,
                                selectedPaymentMethod = toPaymentMethod
                            )
                        }
                    } else {
                        item {
                            PaymentMethodDropdown(
                                colorResId = colorResId,
                                selectedPaymentMethod = selectedPaymentMethod
                            )
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
                            when (dataSettlement) {
                                is DataSettlement.SettlementData -> {
                                    ModelDrawerButton(
                                        modifier = Modifier.padding(
                                            vertical = 10.dp
                                        ),
                                        text = "Apply changes",
                                        wasSuccess = wasSuccess,
                                        colorResId = colorResId,
                                        filledColor = colorResource(colorResId),
                                        textColor = Color.White
                                    ) {
                                        if (amountAsDouble != null && labelState.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            val finance = dataSettlement.financeEntity
                                            val normalizedStart = onCreatedDateTimeState.value

                                            val newFinanceEntity = when (finance) {
                                                is FinanceEntity.Transaction -> finance.copy(
                                                    amount = amountAsDouble,
                                                    label = labelState.text.toString(),
                                                    description = descriptionState.text.toString(),
                                                    createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                                    tagIcon = tagIconState.value,
                                                    paymentMethod = selectedPaymentMethod.value,
                                                    affectCurrentAccount = affectCurrentAccount.value
                                                )

                                                is FinanceEntity.Goal -> finance.copy(
                                                    amount = amountAsDouble,
                                                    label = labelState.text.toString(),
                                                    description = descriptionState.text.toString(),
                                                    createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                                    tagIcon = tagIconState.value,
                                                    paymentMethod = selectedPaymentMethod.value,
                                                )

                                                is FinanceEntity.Liability -> finance.copy(
                                                    amount = amountAsDouble,
                                                    label = labelState.text.toString(),
                                                    description = descriptionState.text.toString(),
                                                    createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                                    tagIcon = tagIconState.value,
                                                    paymentMethod = selectedPaymentMethod.value,
                                                    affectCurrentAccount = affectCurrentAccount.value
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

                                is DataSettlement.SettlementAdjust -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 10.dp
                                            )
                                            .animateItem(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ModelDrawerButton(
                                            text = "Apply changes",
                                            wasSuccess = wasSettlementSuccess,
                                            colorResId = colorResId,
                                            filledColor = Color.Transparent
                                        ) {
                                            if (amountAsDouble != null
                                                && amountAsDouble
                                                <= (dataSettlement.settlement.financeEntity!!.remainingAmount + dataSettlement.settlement.amount)
                                            ) {
                                                val settlement = dataSettlement.settlement
                                                val financeEntityType =
                                                    when (settlement.financeEntity!!) {
                                                        is FinanceEntity.Transaction -> "TRANSACTION"
                                                        is FinanceEntity.Goal -> "GOAL"
                                                        is FinanceEntity.Liability -> "LIABILITY"
                                                    }
                                                viewModel.updateSettlementData(
                                                    settlement.financeEntity!!.id,
                                                    financeEntityType,
                                                    settlement,
                                                    settlement.copy(
                                                        amount = amountAsDouble,
                                                        description = descriptionState.text.toString(),
                                                        dateTime = onCreatedDateTimeState
                                                            .value.toFirestoreTimestampUtc(),
                                                        tagIcon = tagIconState.value,
                                                        paymentMethod = selectedPaymentMethod.value,
                                                        affectCurrentAccount = affectCurrentAccount.value
                                                    )
                                                )


                                                wasSettlementSuccess.value = State.SUCCESS
                                                userViewModel.showActionNotification(
                                                    "Settlement updated successfully",
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
                                                wasSettlementSuccess.value = State.ERROR
                                            }
                                        }
                                    }
                                }

                                is DataSettlement.SettlementWithdrawal -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 10.dp
                                            )
                                            .animateItem(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ModelDrawerButton(
                                            text = "Apply changes",
                                            wasSuccess = wasSettlementSuccess,
                                            colorResId = colorResId,
                                            filledColor = Color.Transparent
                                        ) {
                                            if (amountAsDouble != null
                                                && amountAsDouble
                                                <= (dataSettlement.withdrawal.financeEntity!!.remainingAmount
                                                        + dataSettlement.withdrawal.amount)
                                            ) {
                                                val withdrawal = dataSettlement.withdrawal
                                                val financeEntityType =
                                                    dataSettlement.financeEntityType

                                                viewModel.updateWithdrawal(
                                                    withdrawal.financeEntity!!.id,
                                                    financeEntityType,
                                                    withdrawal,
                                                    withdrawal.copy(
                                                        amount = amountAsDouble,
                                                        description = descriptionState.text.toString(),
                                                        createdAt = onCreatedDateTimeState
                                                            .value.toFirestoreTimestampUtc(),
                                                        fromPaymentMethod = fromPaymentMethod.value,
                                                        toPaymentMethod = toPaymentMethod.value,
                                                        affectCurrentAccount = affectCurrentAccount.value
                                                    )
                                                )


                                                wasSettlementSuccess.value = State.SUCCESS
                                                userViewModel.showActionNotification(
                                                    "Withdrawal updated successfully",
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
                                                wasSettlementSuccess.value = State.ERROR
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



}

@Composable
fun Receipt(
    dataSettlement: DataSettlement,
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
                when (dataSettlement) {
                    is DataSettlement.SettlementData ->
                        FinanceReceipt(
                            financeEntity = dataSettlement.financeEntity,
                            onEdit = {
                                isUpdateModelBottonOpen.value = true
                            },
                            onDelete = {
                                onShowDeleteDialog.value = true
                            }
                        ) {
                            onShowDialog.value = false
                        }

                    is DataSettlement.SettlementAdjust -> {
                        SettlementReceipt(
                            settlement = dataSettlement.settlement,
                            financeEntity = dataSettlement.settlement.financeEntity!!,
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

                    is DataSettlement.SettlementWithdrawal -> {
                        WithdrawalReceipt(
                            withdrawal = dataSettlement.withdrawal,
                            financeEntity = dataSettlement.withdrawal.financeEntity!!,
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
        dataSettlement = dataSettlement,
        onShowDeleteDialog = onShowDeleteDialog,
    ) {
        when (dataSettlement) {
            is DataSettlement.SettlementData -> {
                viewModel.removeData(dataSettlement.financeEntity)
                userViewModel.showActionNotification("Data deleted successfully", Color.Red)
            }

            is DataSettlement.SettlementAdjust -> {
                val financeEntityType = dataSettlement.financeEntityType

                viewModel.removeSettlementFinance(
                    dataSettlement.settlement.financeEntity!!.id,
                    financeEntityType,
                    dataSettlement.settlement
                )
                userViewModel.showActionNotification("Settlement deleted successfully", Color.Red)
            }

            is DataSettlement.SettlementWithdrawal -> {
                val financeEntityType = dataSettlement.financeEntityType

                viewModel.removeWithdrawalFinance(
                    dataSettlement.withdrawal.financeEntity!!.id,
                    financeEntityType,
                    dataSettlement.withdrawal
                )
                userViewModel.showActionNotification("Withdrawal deleted successfully", Color.Red)
            }
        }
        onShowDialog.value = false
    }

    OnUpdate(
        dataSettlement = dataSettlement,
        viewModel = viewModel,
        userViewModel = userViewModel,
        isUpdateModelBottonOpen = isUpdateModelBottonOpen,
        onShowDialog = onShowDialog
    )
}
