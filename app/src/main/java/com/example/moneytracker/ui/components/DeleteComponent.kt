// Glory be to the name of LORD of hosts
package com.example.moneytracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.moneytracker.R
import com.example.moneytracker.ui.theme.StewardTheme

@Composable
fun DeleteButton(
    title: String,
    paragraph: String,
    containerColor: Color = colorResource(R.color.error_color),
    iconColor: Color = MaterialTheme.colorScheme.background,
    isFilled: Boolean = true,
    onConfirm: () -> Unit,
) {

    val showDialog = remember { mutableStateOf(false) }

    if (isFilled) {
        FilledIconButton(
            onClick = { showDialog.value = true },
            colors = IconButtonDefaults.filledIconButtonColors().copy(
                containerColor = containerColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    } else {
        IconButton(
            onClick = { showDialog.value = true },
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = containerColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    DeleteDialog(
        showDialog = showDialog,
        title = title,
        paragraph = paragraph,
        onConfirm = onConfirm
    )
}

@Composable
fun DeleteDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    paragraph: String,
    onConfirm: () -> Unit,
) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isVisible = remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        isVisible.value = true
                    }

                    AnimatedVisibility(
                        visible = isVisible.value,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                                scaleIn(
                                    initialScale = 0.8f,
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                    ) {
                        Image(
                            painter = painterResource(R.drawable.warning),
                            contentDescription = "Warning",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        paragraph,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text(
                                "Cancel",
                                color = StewardTheme.colors.onSurfaceText
                            )
                        }
                        TextButton(
                            onClick = {
                                onConfirm()
                                showDialog.value = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colorResource(R.color.error_color)
                            )
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
