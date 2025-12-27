// Bless is he who comes in the name of LORD.
package com.example.moneytracker.ui.homeScreen.dataAddition

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moneytracker.R
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.example.moneytracker.ui.theme.autoTextColorChange


private val ICONS_TEXT_SIZE = 11.sp
private val ICON_SIZE = 20.dp
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
    viewModel: HomeScreenViewModel
) {

    var onIconClick by remember { mutableStateOf(ICONS[0].second) }

    if (viewModel.isDescriptionIconVisible) {

        Dialog(onDismissRequest = {
            viewModel.updateIsDescriptionIconVisible(false)
        }) {
            // Draw a rectangle shape with rounded corners inside the dialog
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Select an icon",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .padding(8.dp),
                        columns = GridCells.Fixed(4),
                    ) {
                        items(ICONS.size) { index ->
                            val icon = ICONS[index]

                            var modifier = Modifier
                                .padding(4.dp)

                            modifier = if (onIconClick == icon.second) modifier.border(
                                width = 2.dp,
                                color = Color.autoTextColorChange,
                                shape = CircleShape
                            ) else modifier

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                IconButton(
                                    onClick = {
                                        onIconClick = icon.second
                                    },
                                    modifier = modifier
                                ) {
                                    Image(
                                        painter = painterResource(id = icon.second),
                                        contentDescription = "Icon",
                                        modifier = Modifier.size(ICON_SIZE)
                                    )
                                }

                                Text(
                                    text = icon.first,
                                    fontSize = ICONS_TEXT_SIZE,
                                    style = TextStyle(
                                        textAlign = TextAlign.Center,
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.updateDescriptionIcon(onIconClick)
                                viewModel.updateIsDescriptionIconVisible(false)
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Dismiss")
                        }
                        TextButton(
                            onClick = { viewModel.updateIsDescriptionIconVisible(false) },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}
