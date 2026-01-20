// Hear oh Israel, The LORD our GOD, The LORD is one,
// You shall love the LORD your GOD with all your heart
// and all your soul and with all your mind and
// you shall love your neighbour as yourself
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentStatus
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.example.moneytracker.ui.homeScreen.dataAddition.FONT_WEIGHT
import com.example.moneytracker.ui.homeScreen.dataAddition.MaxWidth
import com.example.moneytracker.ui.theme.autoTextColorChange
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import network.chaintech.kmp_date_time_picker.utils.now

@Composable
fun DatasetReceipt(
    dataset: Dataset,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val datetime = dataset.dateTime.toLocalDateTimeUtc()
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
        dataset.dataType in listOf(DataType.DEBT, DataType.LENT, DataType.GOAL) &&
        dataset.remainingAmount == 0.0
    )
        TextDecoration.LineThrough else
        TextDecoration.None

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
        Text(buildString { repeat(40) { append("-") } })
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
            val deadlineDateTime = dataset.deadlineDateTime.toLocalDateTimeUtc()
            val deadlineDay = deadlineDateTime.day.addZeroIfLessThenTen
            val deadlineMonth = deadlineDateTime.month.name.title.take(3)
            val deadlineYear = deadlineDateTime.year
            val deadlineHour = deadlineDateTime.hour.addZeroIfLessThenTen
            val deadlineMinute = deadlineDateTime.minute.addZeroIfLessThenTen

            val deadlineDate = "$deadlineDay $deadlineMonth $deadlineYear"
            val deadlineTime = "$deadlineHour:$deadlineMinute"

            val adjustStatue = remember { mutableStateOf(dataset.status) }

            LaunchedEffect(LocalDateTime.now()) {
                adjustStatue.value = dataset.status
            }


            Text("")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Status:", fontSize = fontSize, fontWeight = FONT_WEIGHT)
                Text(text = adjustStatue.value.text, fontSize = fontSize)
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
            Text(buildString { repeat(40) { append("-") } })

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
                if (
                    (dataset.dataType == DataType.GOAL &&
                            dataset.status == AdjustmentStatus.COMPLETED &&
                            dataset.remainingAmount == 0.0) || (
                            dataset.dataType != DataType.GOAL &&
                                    dataset.remainingAmount == 0.0)
                ) {
                    AsyncImage(
                        model = R.drawable.done,
                        contentDescription = "done",
                        modifier = Modifier.size(32.dp)
                    )
                } else if (dataset.dataType == DataType.GOAL &&
                    dataset.status == AdjustmentStatus.FAILED &&
                    dataset.remainingAmount != 0.0
                ) {
                    AsyncImage(
                        model = R.drawable.failed,
                        contentDescription = "failed",
                        modifier = Modifier.size(32.dp)
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


            Icon(
                painter = painterResource(dataset.dataType.outlinedIcon),
                contentDescription = dataset.dataType.text,
                tint = color,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .padding(3.dp)
            )

            Image(
                painter = painterResource(dataset.labelIcon),
                contentDescription = dataset.dataType.text,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .padding(3.dp)
            )

            Image(
                painter = painterResource(dataset.paymentMethod.icon),
                contentDescription = dataset.dataType.text,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .padding(3.dp)
            )
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

    val color = when (data.dataType) {
        DataType.DEBT -> colorResource(adjustment.adjustmentType.colorDebt)
        DataType.LENT -> colorResource(adjustment.adjustmentType.colorLent)
        DataType.GOAL -> colorResource(adjustment.adjustmentType.colorAttain)
        else -> colorResource(data.dataType.color)
    }

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
        Text(buildString { repeat(40) { append("-") } })
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val label = when (adjustment.adjustmentType) {
                AdjustmentType.REPAYMENT -> "Repaying:"
                AdjustmentType.ATTAIN -> "Attaining:"
            }

            Text(text = label, fontSize = fontSize, fontWeight = FONT_WEIGHT)
            Text(
                text = data.label,
                textDecoration = textDecoration,
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
            Text(buildString { repeat(40) { append("-") } })

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
            val lastPayment = adjustment[adjustment.size - 1]

            if (data.remainingAmount == 0.0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = R.drawable.done,
                        contentDescription = lastPayment.paymentMethod.text,
                        modifier = Modifier.size(32.dp)
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


            Icon(
                painter = painterResource(adjustment.adjustmentType.icon),
                contentDescription = adjustment.adjustmentType.text,
                tint = color,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .padding(3.dp)
            )

            Image(
                painter = painterResource(adjustment.adjustmentIcon),
                contentDescription = adjustment.adjustmentType.text,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .padding(3.dp)
            )

            Image(
                painter = painterResource(adjustment.paymentMethod.icon),
                contentDescription = adjustment.paymentMethod.text,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .padding(3.dp)
            )
        }
    }
}

@Composable
fun OnDeleteReceipt(
    data: Dataset? = null,
    adjustment: Adjustment? = null,
    onShowDeleteDialog: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    val item = if (adjustment != null && data != null) {
        adjustment.adjustmentType.text
    } else data?.dataType?.text ?: ""

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
                            Text("Cancel", color = Color.autoTextColorChange)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.error_color)
                            )
                        ) {
                            Text("Delete", color = Color.autoTextColorChange)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Receipt(
    dataset: Dataset,
    adjustment: Adjustment? = null,
    onShowDialog: MutableState<Boolean>
) {
    val onShowDeleteDialog = remember { mutableStateOf(false) }
    val viewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()

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
                if (adjustment == null) {
                    DatasetReceipt(
                        dataset = dataset,
                        onEdit = {},
                        onDelete = {
                            onShowDeleteDialog.value = true
                        }
                    ) {
                        onShowDialog.value = false
                    }
                } else {
                    AdjustmentReceipt(
                        adjustment = adjustment,
                        data = dataset,
                        onEdit = {},
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

    OnDeleteReceipt(
        data = dataset,
        adjustment = adjustment,
        onShowDeleteDialog = onShowDeleteDialog,
    ) {
        if (adjustment != null) {
            viewModel.removeAdjustmentDataset(
                dataset.id,
                adjustment
            )
        } else {
            viewModel.removeData(dataset)
        }
        onShowDialog.value = false
        onShowDialog.value = false
    }
}

