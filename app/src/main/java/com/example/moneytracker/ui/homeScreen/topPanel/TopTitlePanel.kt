// =====
// Glory be to the name of LORD of hosts
// =====
package com.example.moneytracker.ui.homeScreen.topPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.ui.components.Current
import com.example.moneytracker.ui.homeScreen.HomeUiState

@Composable
fun TopTitlePanel(
    state: State<HomeUiState>,
    contentColor: Color,
    currentPageColor: Color,
    backgroundColor: Color,
    function: (CurrentTopTitle) -> Unit
) {

    // Text weight
    val fontWeight = FontWeight.Bold
    val fontSize = 13.sp

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth(0.72f)
            .height(46.dp)
            .background(backgroundColor)

    ) {

        TextButton(
            modifier = Modifier.padding(2.dp),
            onClick = {
                function(CurrentTopTitle.TODAY)
            },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = contentColor,
                containerColor = if (state.value.topTitle == CurrentTopTitle.TODAY)
                    currentPageColor else
                    Color.Unspecified,
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Today", fontWeight = fontWeight, fontSize = fontSize)
                if (state.value.topTitle == CurrentTopTitle.TODAY) {
                    Current(contentColor)
                }
            }

        }

        TextButton(
            onClick = {
                function(CurrentTopTitle.YESTERDAY)
            },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = contentColor,
                containerColor = if (state.value.topTitle == CurrentTopTitle.YESTERDAY)
                    currentPageColor else
                    Color.Unspecified,
            )

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Yesterday", fontWeight = fontWeight, fontSize = fontSize)
                if (state.value.topTitle == CurrentTopTitle.YESTERDAY) {
                    Current(contentColor)
                }
            }
        }

        TextButton(
            onClick = {
                function(CurrentTopTitle.ALL)
            },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = contentColor,
                containerColor = if (state.value.topTitle == CurrentTopTitle.ALL)
                    currentPageColor else
                    Color.Unspecified,
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("All", fontWeight = fontWeight, fontSize = fontSize)
                if (state.value.topTitle == CurrentTopTitle.ALL) {
                    Current(contentColor)
                }
            }
        }
    }
}