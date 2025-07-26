// Bless be the name of LORD our GOD

package com.example.moneytracker.ui.authScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R

@Composable
fun AuthTextButton(
    id: Int,
    text: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.testTag(stringResource(id = id))
    ) {
        val poppins = FontFamily(
            Font(R.font.poppins_medium, FontWeight.Medium)
        )

        Text(
            text = stringResource(id = text),
            color = Color.Black,
            fontSize = 15.sp,
            fontFamily = poppins
        )
    }
}


@Composable
fun AuthButton(
    id: Int,
    text: Int,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    val cornerShape = RoundedCornerShape(integerResource(R.integer.authButtonRoundedCornerShape))

    OutlinedButton (
        modifier = modifier.testTag(stringResource(id = id)),
        shape = cornerShape,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorResource(id = R.color.authBtnContainerColor)
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.width(160.dp)
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text),
                modifier = Modifier.size(integerResource(id = R.integer.authButtonIconSize).dp)
            )
            Spacer(modifier = Modifier.width(
                integerResource(id = R.integer.authButtonSpacerWidth).dp))
            Text(text = stringResource(id = text))
        }
    }
}

@Composable
fun AuthFillButton(
    id: Int,
    text: Int,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    val cornerShape = RoundedCornerShape(integerResource(R.integer.authButtonRoundedCornerShape))

    Button(
        modifier = modifier.testTag(stringResource(id = id)),
        shape = cornerShape,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.fillAuthBtnContainerColor).copy(alpha = 0.5f),
            contentColor = colorResource(id = R.color.authBtnContainerColor),
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.width(160.dp)
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text),
                modifier = Modifier.size(integerResource(id = R.integer.authButtonIconSize).dp)
            )
            Spacer(modifier = Modifier.width(
                integerResource(id = R.integer.authButtonSpacerWidth).dp))
            Text(text = stringResource(id = text))
        }
    }
}

@Composable
fun AuthHeader(){
    Column(
        modifier = Modifier.padding(top = 81.dp)
    ) {
        val robotoFont = FontFamily(
            Font(R.font.roboto, FontWeight.Normal)
        )

        Text(
            text = stringResource(R.string.title_money),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = robotoFont
        )
        Text(
            text = stringResource(R.string.title_tracker),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = robotoFont
        )
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


