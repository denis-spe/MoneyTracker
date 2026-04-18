// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind, and
// you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTimeUtc
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatasetReceipt(
    dataset: Dataset,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val datetime = dataset.createdAt.toLocalDateTimeUtc()
    val day = datetime.day.addZeroIfLessThenTen
    val month = datetime.month.name.title
    val year = datetime.year.addZeroIfLessThenTen
    val hour = datetime.hour.addZeroIfLessThenTen
    val minute = datetime.minute.addZeroIfLessThenTen
    val weekDay = datetime.dayOfWeek.name.title

    val time = "${hour}:${minute}"
    val date = "$day $month $year"

    val fontSize = 13.sp
    val color = colorResource(dataset.dataType.color)
    val textDecoration = if (
        (dataset.dataType in listOf(DataType.DEBT, DataType.LENT) &&
                dataset.remainingAmount == 0.0) || dataset.status == Status.OVERDUE
    )
        TextDecoration.LineThrough else
        TextDecoration.None
    val dataAdjust = DataAdjust.Data(dataset)

    Column(
        modifier = Modifier
            .fillMaxWidth(MaxWidth)
            .padding(top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dataset.dataType.text,
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

            val label = when (dataset.dataType) {
                DataType.EARNINGS -> "Received from:"
                DataType.EXPENSE -> "Spent on:"
                DataType.DEBT -> "Debt from:"
                DataType.GOAL -> "Your goal:"
                DataType.LENT -> "Lent to:"
                DataType.SAVINGS -> "Savings for:"
            }

            Text(text = label, fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(
                text = dataset.label,
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
                text = dataset.amount.formatToAmount(),
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
            Text(text = dataset.paymentMethod.text, fontSize = fontSize)
        }

        if (dataset.dataType in listOf(DataType.DEBT, DataType.LENT, DataType.GOAL)
            && dataset.adjustment.isNotEmpty()
        ) {
            val adjustment = dataset.adjustment
            val lastPayment = adjustment[adjustment.size - 1]
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
                Text(text = dataset.remainingAmount.formatToAmount(), fontSize = fontSize)
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

        if (dataset.dataType == DataType.GOAL) {
            val deadlineDateTime = dataset.routine.deadlineDateTime.toLocalDateTimeUtc()
            val deadlineDay = deadlineDateTime.day.addZeroIfLessThenTen
            val deadlineMonth = deadlineDateTime.month.name.title.take(3)
            val deadlineYear = deadlineDateTime.year
            val deadlineHour = deadlineDateTime.hour.addZeroIfLessThenTen
            val deadlineMinute = deadlineDateTime.minute.addZeroIfLessThenTen

            val deadlineDate = "$deadlineDay $deadlineMonth $deadlineYear"
            val deadlineTime = "$deadlineHour:$deadlineMinute"

            val adjustStatue = remember { mutableStateOf(dataset.status) }

            LaunchedEffect(deadlineDateTime) {
                val now = LocalDateTime.now()
                val delayMillis = ChronoUnit.MILLIS.between(
                    now.toJavaLocalDateTime(),
                    deadlineDateTime.toJavaLocalDateTime()
                )

                if (delayMillis > 0) {
                    delay(delayMillis)
                    adjustStatue.value = dataset.status
                } else {
                    adjustStatue.value = dataset.status
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

        if (dataset.description.isNotBlank()) {
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
                            text = dataset.description,
                            fontSize = fontSize,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (dataset.dataType in listOf(DataType.DEBT, DataType.LENT, DataType.GOAL)) {

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
                Icon(
                    painter = painterResource(dataset.dataType.outlinedIcon),
                    contentDescription = dataset.dataType.text,
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
                    painter = painterResource(dataset.tagIcon.icon),
                    contentDescription = dataset.dataType.text,
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
                    painter = painterResource(dataset.paymentMethod.icon),
                    contentDescription = dataset.dataType.text,
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
    data: Dataset,
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
        data.remainingAmount == 0.0
    )
        TextDecoration.LineThrough else
        TextDecoration.None
    val title = when (data.dataType) {
        DataType.DEBT -> "Repaid Debt"
        DataType.LENT -> "Repaid Loan"
        DataType.GOAL -> "Attained Goal"
        else -> throw Exception("Unknown data type")
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
                else -> throw Exception("Unknown adjustment type")
            }

            Text(text = label, fontSize = fontSize, fontWeight = FONT_WEIGHT)
            val labelState = remember { mutableStateOf("") }
            labelState.value = data.label
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

        if (data.dataType in listOf(DataType.DEBT, DataType.LENT, DataType.GOAL)
            && data.adjustment.isNotEmpty()
        ) {
            val adjustment = data.adjustment
            val lastPayment = adjustment[adjustment.size - 1]
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
                Text(text = data.remainingAmount.formatToAmount(), fontSize = fontSize)
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

        if (data.dataType in listOf(DataType.DEBT, DataType.LENT, DataType.GOAL)
            && data.adjustment.isNotEmpty()
        ) {
            val adjustment = data.adjustment
            adjustment[adjustment.size - 1]

            if (data.remainingAmount == 0.0) {
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
        is DataAdjust.Data -> dataAdjust.dataset.dataType.text
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

@RequiresApi(Build.VERSION_CODES.O)
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
    remember { mutableStateOf<Dataset?>(null) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    rememberTextFieldState()
    val lazyState = rememberLazyListState()

    val dataType = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.dataType.text
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.text
    }

    val colorResId = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.dataType.color
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.color
    }

    val icon = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.dataType.filledIcon
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.icon
    }

    val description = when (dataAdjust) {
        is DataAdjust.Data -> "Updating ${dataAdjust.dataset.label} " +
                dataAdjust.dataset.dataType.text

        is DataAdjust.Adjust -> "Updating ${dataAdjust.adjustment.dataset?.label} " +
                dataAdjust.adjustment.adjustmentType.text
    }

    val color = colorResource(colorResId)

    LaunchedEffect(dataAdjust, isUpdateModelBottonOpen.value) {
        if (isUpdateModelBottonOpen.value) {
            when (dataAdjust) {
                is DataAdjust.Data -> {
                    val dataset = dataAdjust.dataset
                    amountState.setTextAndPlaceCursorAtEnd(dataset.amount.toString())
                    labelState.setTextAndPlaceCursorAtEnd(dataset.label)
                    descriptionState.setTextAndPlaceCursorAtEnd(dataset.description)
                    localDateTimeState.value = dataset.createdAt.toLocalDateTimeUtc()
                    endLocalDateTimeState.value =
                        dataset.routine.deadlineDateTime.toLocalDateTimeUtc()
                    tagIconState.value = dataset.tagIcon
                    selectedPaymentMethod.value = dataset.paymentMethod
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
                        contentDescription = dataType,
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
                        when (dataAdjust) {
                            is DataAdjust.Data -> {
                                Row(
                                    modifier = Modifier.animateItem()
                                ) {
                                    ModelDrawerTextField(
                                        state = labelState,
                                        title = "Label",
                                        description = "Add a label for the given amount",
                                        placeholder = dataAdjust.dataset.label,
                                        colorResId = colorResId,
                                        wasSuccess = wasSuccess,
                                        textLength = MAX_LABEL_LENGTH,
                                        displayText = displayLabel
                                    )
                                }
                            }

                            is DataAdjust.Adjust -> {}
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
                                dataAdjust.dataset.dataType == DataType.GOAL
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
                                            val dataset = dataAdjust.dataset
                                            val newDataset = dataset.copy(
                                                amount = amountAsDouble,
                                                label = labelState.text.toString(),
                                                description = descriptionState.text.toString(),
                                                createdAt = localDateTimeState.value.toFirestoreTimestampUtc(),
                                                tagIcon = tagIconState.value,
                                                paymentMethod = selectedPaymentMethod.value,
                                                routine = if (dataset.dataType == DataType.GOAL) {
                                                    dataset.routine.copy(
                                                        startDateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                                        deadlineDateTime = endLocalDateTimeState.value.toFirestoreTimestampUtc()
                                                    )
                                                } else {
                                                    dataset.routine
                                                }
                                            )
                                            viewModel.updateData(
                                                dataset,
                                                newDataset
                                            )

                                            if (dataset.dataType == DataType.GOAL) {
                                                viewModel.beginTheWork(newDataset)
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
                                                <= dataAdjust.adjustment.dataset!!.remainingAmount
                                            ) {
                                                val adjustment = dataAdjust.adjustment
                                                viewModel.updateAdjustmentData(
                                                    adjustment.dataset!!,
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
            is DataAdjust.Data -> dataAdjust.dataset.amount
            is DataAdjust.Adjust -> dataAdjust.adjustment.amount
        }
        val label = if (dataAdjust is DataAdjust.Data) {
            dataAdjust.dataset.label
        } else ""

        val description = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.dataset.description
            is DataAdjust.Adjust -> dataAdjust.adjustment.description
        }

        val dateTime = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.dataset.createdAt
            is DataAdjust.Adjust -> dataAdjust.adjustment.dateTime
        }

        val tagIcon = when (dataAdjust) {
            is DataAdjust.Data -> dataAdjust.dataset.tagIcon
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

@RequiresApi(Build.VERSION_CODES.O)
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
                        DatasetReceipt(
                            dataset = dataAdjust.dataset,
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
                            data = dataAdjust.adjustment.dataset!!,
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
                viewModel.removeData(dataAdjust.dataset)
                userViewModel.showActionNotification("Data deleted successfully", Color.Red)
            }

            is DataAdjust.Adjust -> {
                viewModel.removeAdjustmentDataset(
                    dataAdjust.adjustment.dataset!!.id,
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

