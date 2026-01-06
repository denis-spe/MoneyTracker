// Bless be the name of the LORD of host
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.helper.State
import com.example.moneytracker.ui.theme.autoTextColorChange


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionFloatingButton(
    updateOnModelBottomSheetShow: (Boolean) -> Unit,
) {
    IconButton(
        onClick = {
            updateOnModelBottomSheetShow(true)
        },
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Add data",
            tint = Color.autoTextColorChange,
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun ModelDrawerButton(
    text: String,
    colorResId: Int,
    wasSuccess: MutableState<State>? = null,
    shape: Shape = ButtonDefaults.outlinedShape,
    onClick: () -> Unit,
) {
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp

    val color = if (wasSuccess != null && wasSuccess.value == State.ERROR)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(height),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            contentColor = color,
            containerColor = color.copy(alpha = 0.2f),
        ),
        shape = shape,
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            fontSize = fontSize
        )
    }
}