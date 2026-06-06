// Bless be the name of the LORD of host
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.DoDisturbOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.theme.StewardTheme

private val FLOAT_BUTTON_SIZE = 45.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionFloatingButton(
    viewModel: HomeViewModel = hiltViewModel(),
    uiState: HomeUiState,
    isLoading: Boolean = false
) {
    val isDatasetBottomSheetOpen = uiState.isDatasetBottomSheetOpen
    val isSettlementBottomSheetOpen = uiState.isSettlementBottomSheetOpen


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = FLOAT_BUTTON_SIZE)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = 43.dp)
            )
        } else {
            FloatingActionButton(
                onClick = {
                    viewModel.updateOnDatasetModelBottomSheetShow(true)
                },
                shape = CircleShape,
                modifier = Modifier.size(FLOAT_BUTTON_SIZE),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp),
                containerColor = StewardTheme.colors.primaryAccent,
            ) {
                Icon(
                    imageVector = if (isDatasetBottomSheetOpen) Icons.Default.Clear else Icons.Default.Add,
                    contentDescription = "Add data",
                    tint = StewardTheme.colors.accentContent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FloatingActionButton(
                onClick = {
                    viewModel.updateOnAdjustModelBottomSheetShow(true)
                },
                shape = CircleShape,
                modifier = Modifier.size(43.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp),
                containerColor = StewardTheme.colors.primaryAccent
            ) {
                Icon(
                    imageVector = if (isSettlementBottomSheetOpen) Icons.Outlined.DoDisturbOn
                    else Icons.Default.Adjust,
                    contentDescription = "Add settlement",
                    tint = StewardTheme.colors.accentContent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ModelDrawerButton(
    text: String,
    colorResId: Int,
    modifier: Modifier = Modifier,
    icon: Int? = null,
    wasSuccess: MutableState<State>? = null,
    shape: Shape = ButtonDefaults.outlinedShape,
    filledColor: Color? = null,
    textColor: Color? = null,
    fontSize: TextUnit = 15.sp,
    onClick: () -> Unit,
) {
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp

    val color = if (wasSuccess != null && wasSuccess.value == State.ERROR)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(bottom = 10.dp)
            .height(height),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            contentColor = textColor ?: color,
            containerColor = filledColor ?: color.copy(alpha = 0.2f),
        ),
        shape = shape,
        border = BorderStroke(3.dp, color)
    ) {
        if (icon != null) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = icon,
                    contentDescription = text,
                    modifier = Modifier.size(MODEL_DRAWER_ICON_SIZE)
                )

                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                Text(
                    text = text,
                    fontSize = fontSize,
                    fontWeight = FONT_WEIGHT
                )

            }
        } else {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FONT_WEIGHT
            )
        }
    }
}

