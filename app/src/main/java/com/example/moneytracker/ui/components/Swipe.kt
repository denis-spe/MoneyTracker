// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun Swipe(
    onStartToEnd: () -> Unit,
    onEndToStart: () -> Unit,
    modifier: Modifier = Modifier,
    startToEndBgColor: Color = Color(0xFF4CAF50).copy(alpha = 0.8f),
    endToStartBgColor: Color = Color(0xFFF44336).copy(alpha = 0.8f),
    startToEndIcon: ImageVector = Icons.Filled.Edit,
    startToEndText: String = "Update",
    endToStartIcon: ImageVector = Icons.Filled.Delete,
    endToStartText: String = "Delete",
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberSwipeToDismissBoxState()

    val backgroundColor by animateColorAsState(
        targetValue = when (state.dismissDirection) {
            SwipeToDismissBoxValue.StartToEnd -> startToEndBgColor // Green
            SwipeToDismissBoxValue.EndToStart -> endToStartBgColor // Red
            else -> Color.Transparent
        },
        label = "SwipeBackground"
    )

    SwipeToDismissBox(
        state = state,
        modifier = modifier.fillMaxSize(),
        onDismiss = { direction ->
            when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> onStartToEnd()
                SwipeToDismissBoxValue.EndToStart -> onEndToStart()
                else -> {}
            }
            // Reset to settled state so it doesn't stay "dismissed"
            coroutineScope.launch {
                state.reset()
            }
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp),
                contentAlignment = when (state.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    else -> Alignment.Center
                }
            ) {
                when (state.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = startToEndIcon,
                                contentDescription = startToEndText,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = startToEndText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = endToStartText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = endToStartIcon,
                                contentDescription = endToStartText,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        }
    ) {
        content()
    }
}
