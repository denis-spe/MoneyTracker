package com.example.moneytracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.ui.LoadingScreen
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.ProfileImage
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    navController: NavController? = null,
    userId: String = "",
    viewModel: SettingsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val themeConfig by viewModel.themeConfig.collectAsState()
    val dynamicColor by viewModel.dynamicColor.collectAsState()
    val uiState by userViewModel.uiState.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var pickedColor by remember { mutableStateOf(Color.Unspecified) }
    var showDialogForField by remember { mutableStateOf<String?>(null) }


    if (uiState.isLoading) {
        LoadingScreen(
            user = userState != null,
            navController = navController,
            currentUserId = userId
        )
    }

    LaunchedEffect(Unit) {
        userViewModel.navigationEvents.collect {
            onBackClick() // Simple back if we are in settings, or handle globally
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            UserCredentialSettings(
                userViewModel = userViewModel,
                onLogIn = onLoginClick,
                onLogOut = { userViewModel.handleLogout() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.padding(8.dp))

            ThemeSettings(
                themeConfig = themeConfig,
                onThemeConfigChange = viewModel::setThemeConfig
            )

            DynamicColorSettings(
                dynamicColor = dynamicColor,
                onDynamicColorChange = viewModel::setDynamicColor
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = "Custom Theme Colors",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val fields =
                    listOf(
                        "Theme Color",
                        "Custom Background",
                        "Content",
                        "Auto Background",
                        "Auto Text"
                    )

                fields.forEach { field ->

                    Row(
                        modifier = Modifier.clickable {
                            showDialogForField = field
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val color = when (field) {
                            "Theme Color" -> MoneyTrackerTheme.colors.themeColor
                            "Custom Background" -> MoneyTrackerTheme.colors.customBackground
                            "Content" -> MoneyTrackerTheme.colors.contentColor
                            "Auto Background" -> MoneyTrackerTheme.colors.autoBackground
                            "Auto Text" -> MoneyTrackerTheme.colors.autoText
                            else -> Color.Unspecified
                        }
                        Box(
                            modifier = Modifier
                                .width(23.dp)
                                .height(20.dp)
                                .padding(end = 3.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(field)
                    }
                }
            }

            if (showDialogForField != null) {
                val field = showDialogForField!!
                Dialog(onDismissRequest = { showDialogForField = null }) {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Pick $field", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(16.dp))

                            ColorSettings(onColorChange = { pickedColor = it })

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    applyColor(field, pickedColor, viewModel)
                                    showDialogForField = null
                                },
                                enabled = pickedColor != Color.Unspecified,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = pickedColor
                                )
                            ) {
                                Text("Apply")
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        }
    }
}

private fun applyColor(field: String, color: Color, viewModel: SettingsViewModel) {
    val colorLong = color.value.toLong()
    viewModel.setDynamicColor(false)
    when (field) {
        "Theme Color" -> viewModel.setThemeColor(colorLong)
        "Custom Background" -> viewModel.setCustomBackground(colorLong)
        "Content" -> viewModel.setContentColor(colorLong)
        "Auto Background" -> viewModel.setAutoBackground(colorLong)
        "Auto Text" -> viewModel.setAutoText(colorLong)
    }
}

@Composable
fun CredentialButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(MoneyTrackerTheme.colors.themeColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Log out icon",
                tint = MoneyTrackerTheme.colors.contentColor,
                modifier = Modifier
                    .size(30.dp)
                    .padding(5.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )

    }
}

@Composable
fun UserCredentialSettings(
    userViewModel: UserViewModel,
    onLogOut: () -> Unit = { },
    onLogIn: () -> Unit = { }
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val email = userState?.email
    val names = userState?.displayName
        ?.split(" ")
        ?.joinToString(" ")
    val profilePictureUrl = userState?.photoUrl

    Column {
        Column(
            modifier = Modifier.padding(bottom = 3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileImage(
                accountSpecificUrl = profilePictureUrl,
                currentAccountId = userState?.uid ?: "",
                size = 70,
            )

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            names?.let { userName ->
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium,
                )
            }.run {
                Text(
                    text = "Guest",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        email?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }.run {
            Text(
                text = buildString {
                    append("Please register to sync your data across devices and access additional features.")
                },
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
        ) {
            if (email.isNullOrEmpty()) {
                CredentialButton(
                    onClick = { onLogIn() },
                    text = "Login",
                    icon = Icons.AutoMirrored.Filled.Login
                )
            }

            CredentialButton(
                onClick = { onLogOut() },
                text = "Logout",
                icon = Icons.AutoMirrored.Filled.Logout
            )
        }
    }
}

@Composable
fun ColorSettings(onColorChange: (Color) -> Unit) {
    val controller = rememberColorPickerController()
    HsvColorPicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(10.dp),
        controller = controller,
        onColorChanged = { colorEnvelope ->
            if (colorEnvelope.fromUser) {
                onColorChange(colorEnvelope.color)
            }
        }
    )
}

@Composable
fun ThemeSettings(
    themeConfig: ThemeConfig,
    onThemeConfigChange: (ThemeConfig) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        ThemeConfig.entries.forEach { theme ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (theme == themeConfig),
                        onClick = { onThemeConfigChange(theme) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (theme == themeConfig),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MoneyTrackerTheme.colors.themeColor,
                    ),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = when (theme) {
                        ThemeConfig.SYSTEM -> "System default"
                        ThemeConfig.LIGHT -> "Light"
                        ThemeConfig.DARK -> "Dark"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun DynamicColorSettings(
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDynamicColorChange(!dynamicColor) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Dynamic color",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Use colors from your wallpaper (Android 12+)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = dynamicColor,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MoneyTrackerTheme.colors.themeColor,
                checkedTrackColor = MoneyTrackerTheme.colors.themeColor.copy(alpha = 0.5f),
            )
        )
    }
}
