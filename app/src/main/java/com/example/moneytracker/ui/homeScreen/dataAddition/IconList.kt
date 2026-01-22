// Bless is he who comes in the name of LORD.
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moneytracker.R
import com.example.moneytracker.ui.theme.autoTextColorChange


private val ICONS_TEXT_SIZE = 11.sp
val ICON_SIZE = 20.dp
private val ICONS = listOf(
    Pair("description", R.drawable.description),
    Pair("milk carton", R.drawable.milk_carton),
    Pair("mcdonald french fries", R.drawable.mcdonald_french_fries),
    Pair("pizza", R.drawable.salami_pizza),
    Pair("tomato", R.drawable.tomato),
    Pair("rent", R.drawable.rent),
    Pair("renting", R.drawable.renting),
    Pair("watermelon", R.drawable.watermelon),
    Pair("rice bowl", R.drawable.rice_bowl),
    Pair("banana", R.drawable.banana),
    Pair("beef", R.drawable.beef),
    Pair("biscuits", R.drawable.biscuits),
    Pair("bread", R.drawable.bread),
    Pair("burger", R.drawable.hamburger),
    Pair("fried chicken", R.drawable.fried_chicken),
    Pair("eggs", R.drawable.eggs),
    Pair("cola", R.drawable.cola),
)

@Composable
fun IconList(
    onConfirm: MutableState<Pair<String, Int>>,
    onDialogOpen: MutableState<Boolean>
) {

    var selectionIcon by remember {
        mutableStateOf(
            Pair("description", R.drawable.description)
        )
    }

    if (onDialogOpen.value) {

        Dialog(onDismissRequest = { onDialogOpen.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Select an icon", modifier = Modifier.padding(16.dp))

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .padding(8.dp),
                    columns = GridCells.Fixed(4),
                ) {
                    items(ICONS.size, key = { it }) { idx ->
                        val icon = ICONS[idx]
                        val borderModifier =
                            if (selectionIcon.second == icon.second)
                                Modifier.border(
                                    2.dp,
                                    Color.autoTextColorChange,
                                    CircleShape
                                )
                            else Modifier

                        Column(
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(250),
                                fadeOutSpec = tween(100),
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                modifier = borderModifier.padding(4.dp),
                                onClick = {
                                    selectionIcon = icon
                                }
                            ) {
                                Image(
                                    painter = painterResource(icon.second),
                                    contentDescription = null,
                                    modifier = Modifier.size(ICON_SIZE)
                                )
                            }

                            Text(
                                text = icon.first,
                                fontSize = ICONS_TEXT_SIZE,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onDialogOpen.value = false }) {
                        Text("Dismiss")
                    }

                    TextButton(
                        onClick = {
                            onDialogOpen.value = false
                            onConfirm.value = selectionIcon
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
        }
    }
}
