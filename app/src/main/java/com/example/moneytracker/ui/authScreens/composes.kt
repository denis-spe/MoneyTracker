// Bless be the name of LORD our GOD

package com.example.moneytracker.ui.authScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R

val SHAPE = RoundedCornerShape(20)

@Composable
fun AuthButton(
    id: Int,
    text: Int,
    icon: Int,
    onClick: () -> Unit,
){
    OutlinedButton (
        modifier = Modifier.testTag(stringResource(id = id)),
        shape = SHAPE,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorResource(id = R.color.authBtnContainerColor)
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = text))
        }
    }
}

@Composable
fun AuthFillButton(
    id: Int,
    text: Int,
    icon: Int,
    onClick: () -> Unit,
){
    Button(
        modifier = Modifier.testTag(stringResource(id = id)),
        shape = SHAPE,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.fillAuthBtnContainerColor).copy(alpha = 0.5f),
            contentColor = colorResource(id = R.color.authBtnContainerColor),
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = text))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthButtonPreview(){
    AuthButton(
        id = R.string.google_text,
        text = R.string.google_text,
        icon = R.drawable.google_icon,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun AuthFillButtonPreview(){
    AuthFillButton(
        id = R.string.google_text,
        text = R.string.google_text,
        icon = R.drawable.google_icon,
        onClick = {}
    )
}


