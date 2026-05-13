package com.example.moneytracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.LoadingScreen
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.ProfileImage
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.HueSlider
import com.github.skydoves.colorpicker.compose.SaturationSlider
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
    var configModeIsDark by remember { mutableStateOf(false) }

    // Light mode states
    val lSecondarySurface by viewModel.lightSecondarySurface.collectAsState()
    val lAccentContent by viewModel.lightAccentContent.collectAsState()
    val lOnSurfaceText by viewModel.lightOnSurfaceText.collectAsState()
    val lPrimaryAccent by viewModel.lightPrimaryAccent.collectAsState()

    // Dark mode states
    val dSecondarySurface by viewModel.darkSecondarySurface.collectAsState()
    val dAccentContent by viewModel.darkAccentContent.collectAsState()
    val dOnSurfaceText by viewModel.darkOnSurfaceText.collectAsState()
    val dPrimaryAccent by viewModel.darkPrimaryAccent.collectAsState()

    val darkBackgroundColor = Color(0xFF282626)
    val lightBackgroundColor = Color(0xFFE0DDDD)

    if (uiState.isLoading) {
        LoadingScreen(
            user = userState != null,
            navController = navController,
            currentUserId = userId
        )
    }

    LaunchedEffect(Unit) {
        userViewModel.navigationEvents.collect {
            onBackClick()
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

            SecondaryTabRow(
                selectedTabIndex = if (configModeIsDark) 1 else 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                containerColor = MoneyTrackerTheme.colors.secondarySurface,
                contentColor = MoneyTrackerTheme.colors.primaryAccent,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(if (configModeIsDark) 1 else 0),
                        color = MoneyTrackerTheme.colors.primaryAccent
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = !configModeIsDark,
                    onClick = { configModeIsDark = false },
                    text = { Text("Light Mode") },
                    selectedContentColor = MoneyTrackerTheme.colors.primaryAccent,
                    unselectedContentColor = MoneyTrackerTheme.colors.onSurfaceText
                )
                Tab(
                    selected = configModeIsDark,
                    onClick = { configModeIsDark = true },
                    text = { Text("Dark Mode") },
                    selectedContentColor = MoneyTrackerTheme.colors.primaryAccent,
                    unselectedContentColor = MoneyTrackerTheme.colors.onSurfaceText
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val fields =
                    listOf(
                        "Primary Accent",
                        "Secondary Surface",
                        "Accent Content",
                        "On Surface Text"
                    )

                fields.forEach { field ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (showDialogForField == field)
                                    MoneyTrackerTheme.colors.primaryAccent.copy(alpha = 0.2f)
                                else
                                    Color.Transparent
                            )
                            .clickable {
                                showDialogForField = field
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val color = if (configModeIsDark) {
                            when (field) {
                                "Primary Accent" -> dPrimaryAccent?.let { Color(it.toULong()) }
                                    ?: Color(0xFF59A5D8)

                                "Secondary Surface" -> dSecondarySurface?.let { Color(it.toULong()) }
                                    ?: darkBackgroundColor.copy(alpha = 0.5f)

                                "Accent Content" -> dAccentContent?.let { Color(it.toULong()) }
                                    ?: Color.White.copy(alpha = 0.8f)

                                "On Surface Text" -> dOnSurfaceText?.let { Color(it.toULong()) }
                                    ?: Color.White

                                else -> Color.Unspecified
                            }
                        } else {
                            when (field) {
                                "Primary Accent" -> lPrimaryAccent?.let { Color(it.toULong()) }
                                    ?: Color(0xFF688E26)

                                "Secondary Surface" -> lSecondarySurface?.let { Color(it.toULong()) }
                                    ?: lightBackgroundColor.copy(alpha = 0.5f)

                                "Accent Content" -> lAccentContent?.let { Color(it.toULong()) }
                                    ?: Color.Black.copy(alpha = 0.8f)

                                "On Surface Text" -> lOnSurfaceText?.let { Color(it.toULong()) }
                                    ?: Color.Black

                                else -> Color.Unspecified
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = field
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (configModeIsDark) viewModel.resetDarkColors()
                        else viewModel.resetLightColors()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.error_color).copy(alpha = 0.1f),
                        contentColor = colorResource(R.color.error_color)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset to Defaults")
                }
            }

            if (showDialogForField != null) {
                val field = showDialogForField!!
                Dialog(onDismissRequest = { showDialogForField = null }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pick $field (${if (configModeIsDark) "Dark" else "Light"})",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(
                                modifier = Modifier.fillMaxHeight(0.6f)
                            ) {
                                item {
                                    ColorSettings(onColorChange = { pickedColor = it })
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            Button(
                                onClick = {
                                    applyColor(field, pickedColor, viewModel, configModeIsDark)
                                    showDialogForField = null
                                    pickedColor = Color.Unspecified
                                },
                                enabled = pickedColor != Color.Unspecified,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pickedColor != Color.Unspecified)
                                        pickedColor
                                    else
                                        MoneyTrackerTheme.colors.primaryAccent
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

private fun applyColor(field: String, color: Color, viewModel: SettingsViewModel, isDark: Boolean) {
    val colorLong = color.value.toLong()
    viewModel.setDynamicColor(false)
    if (isDark) {
        when (field) {
            "Primary Accent" -> viewModel.setDarkPrimaryAccent(colorLong)
            "Secondary Surface" -> viewModel.setDarkSecondarySurface(colorLong)
            "Accent Content" -> viewModel.setDarkAccentContent(colorLong)
            "On Surface Text" -> viewModel.setDarkOnSurfaceText(colorLong)
        }
    } else {
        when (field) {
            "Primary Accent" -> viewModel.setLightPrimaryAccent(colorLong)
            "Secondary Surface" -> viewModel.setLightSecondarySurface(colorLong)
            "Accent Content" -> viewModel.setLightAccentContent(colorLong)
            "On Surface Text" -> viewModel.setLightOnSurfaceText(colorLong)
        }
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
                .background(MoneyTrackerTheme.colors.primaryAccent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Log out icon",
                tint = MoneyTrackerTheme.colors.accentContent,
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
    val fillMaxWidth = 0.8f

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

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Alpha value")
    }
    AlphaSlider(
        modifier = Modifier
            .fillMaxWidth(fillMaxWidth)
            .padding(10.dp)
            .height(35.dp),
        controller = controller
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Brightness")
    }
    BrightnessSlider(
        modifier = Modifier
            .fillMaxWidth(fillMaxWidth)
            .padding(10.dp)
            .height(35.dp),
        controller = controller
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Hue")
    }
    HueSlider(
        modifier = Modifier
            .fillMaxWidth(fillMaxWidth)
            .padding(10.dp)
            .height(35.dp),
        controller = controller
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Saturation")
    }
    SaturationSlider(
        modifier = Modifier
            .fillMaxWidth(fillMaxWidth)
            .padding(10.dp)
            .height(35.dp),
        controller = controller
    )

    Row(
        modifier = Modifier.fillMaxWidth(fillMaxWidth)
    ) {
        Text("Alpha Tile")
    }
    AlphaTile(
        modifier = Modifier
            .fillMaxWidth(fillMaxWidth)
            .padding(10.dp)
            .height(35.dp),
        controller = controller
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
                        selectedColor = MoneyTrackerTheme.colors.primaryAccent,
                    ),
                    onClick = null
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
                checkedThumbColor = MoneyTrackerTheme.colors.primaryAccent,
                checkedTrackColor = MoneyTrackerTheme.colors.primaryAccent.copy(alpha = 0.5f),
            )
        )
    }
}
