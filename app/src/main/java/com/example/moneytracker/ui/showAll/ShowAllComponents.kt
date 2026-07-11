// Love the LORD your GOD with all your soul and with all your mind and
// with all your might and love your neighbor as yourself
package com.example.moneytracker.ui.showAll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.twotone.ShowChart
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun ShowAllMoreOption(
    showHelp: MutableState<Boolean>,
    showStats: MutableState<Boolean>,
    showVisualAnalysis: MutableState<Boolean>,
    isExpanded: MutableState<Boolean>,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = isExpanded.value,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Statistics") },
            onClick = { showStats.value = true },
            leadingIcon = {
                Icon(
                    imageVector = Icons.TwoTone.Insights,
                    contentDescription = "Statistics"
                )
            }
        )

        DropdownMenuItem(
            text = { Text("Visual Analysis") },
            onClick = { showVisualAnalysis.value = true },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.TwoTone.ShowChart,
                    contentDescription = "visualization"
                )
            }
        )

        DropdownMenuItem(
            text = { Text("Help") },
            onClick = { showHelp.value = true },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = "Help"
                )
            }
        )
    }
}
