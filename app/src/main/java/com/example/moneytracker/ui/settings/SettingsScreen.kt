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
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.LoadingScreen
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.ProfileImage
import com.example.moneytracker.ui.theme.StewardTheme
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
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            UserCredentialSettings(
                userViewModel = userViewModel,
                onLogIn = onLoginClick,
                onLogOut = { userViewModel.handleLogout() }
            )

            SettingsSection(
                title = "Appearance",
                icon = Icons.Default.Palette
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeSettings(
                        themeConfig = themeConfig,
                        onThemeConfigChange = viewModel::setThemeConfig
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    DynamicColorSettings(
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = viewModel::setDynamicColor
                    )
                }
            }

            SettingsSection(
                title = "Custom Theme Colors",
                icon = Icons.Default.ColorLens
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryTabRow(
                        selectedTabIndex = if (configModeIsDark) 1 else 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = StewardTheme.colors.primary,
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(if (configModeIsDark) 1 else 0),
                                color = StewardTheme.colors.primary
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = !configModeIsDark,
                            onClick = { configModeIsDark = false },
                            text = { Text("Light") },
                            selectedContentColor = StewardTheme.colors.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Tab(
                            selected = configModeIsDark,
                            onClick = { configModeIsDark = true },
                            text = { Text("Dark") },
                            selectedContentColor = StewardTheme.colors.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val fields = listOf(
                            "Primary",
                            "Secondary Surface",
                            "Accent Content",
                            "On Surface Text"
                        )

                        fields.forEach { field ->
                            ColorRow(
                                field = field,
                                isDark = configModeIsDark,
                                viewModel = viewModel,
                                isSelected = showDialogForField == field,
                                onClick = { showDialogForField = field }
                            )
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
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset to Defaults", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (showDialogForField != null) {
                val field = showDialogForField!!
                ColorPickerDialog(
                    field = field,
                    isDark = configModeIsDark,
                    pickedColor = pickedColor,
                    onColorChange = { pickedColor = it },
                    onDismiss = {
                        showDialogForField = null
                        pickedColor = Color.Unspecified
                    },
                    onApply = {
                        applyColor(field, pickedColor, viewModel, configModeIsDark)
                        showDialogForField = null
                        pickedColor = Color.Unspecified
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ColorRow(
    field: String,
    isDark: Boolean,
    viewModel: SettingsViewModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val themeState by viewModel.themeState.collectAsState()

    val color = if (isDark) {
        when (field) {
            "Primary" -> themeState.darkPrimaryAccent?.let { Color(it.toULong()) } ?: Color(
                0xFF59A5D8
            )

            "Secondary Surface" -> themeState.darkSecondarySurface?.let { Color(it.toULong()) }
                ?: Color(0xFF282626).copy(alpha = 0.5f)

            "Accent Content" -> themeState.darkAccentContent?.let { Color(it.toULong()) }
                ?: Color.White.copy(alpha = 0.8f)

            "On Surface Text" -> themeState.darkOnSurfaceText?.let { Color(it.toULong()) }
                ?: Color.White

            else -> Color.Unspecified
        }
    } else {
        when (field) {
            "Primary" -> themeState.lightPrimaryAccent?.let { Color(it.toULong()) } ?: Color(
                0xFF688E26
            )

            "Secondary Surface" -> themeState.lightSecondarySurface?.let { Color(it.toULong()) }
                ?: Color(0xFFE0DDDD).copy(alpha = 0.5f)

            "Accent Content" -> themeState.lightAccentContent?.let { Color(it.toULong()) }
                ?: Color.Black.copy(alpha = 0.8f)

            "On Surface Text" -> themeState.lightOnSurfaceText?.let { Color(it.toULong()) }
                ?: Color.Black

            else -> Color.Unspecified
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = field,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer(rotationZ = 180f),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ColorPickerDialog(
    field: String,
    isDark: Boolean,
    pickedColor: Color,
    onColorChange: (Color) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Customize $field",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isDark) "Dark Mode Palette" else "Light Mode Palette",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxHeight(0.6f)) {
                    ColorSettings(onColorChange = onColorChange)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onApply,
                        enabled = pickedColor != Color.Unspecified,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (pickedColor != Color.Unspecified) pickedColor else StewardTheme.colors.primary
                        )
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun applyColor(field: String, color: Color, viewModel: SettingsViewModel, isDark: Boolean) {
    val colorLong = color.value.toLong()
    viewModel.setDynamicColor(false)
    if (isDark) {
        when (field) {
            "Primary" -> viewModel.setDarkPrimaryAccent(colorLong)
            "Secondary Surface" -> viewModel.setDarkSecondarySurface(colorLong)
            "Accent Content" -> viewModel.setDarkAccentContent(colorLong)
            "On Surface Text" -> viewModel.setDarkOnSurfaceText(colorLong)
        }
    } else {
        when (field) {
            "Primary" -> viewModel.setLightPrimaryAccent(colorLong)
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
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
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
    val displayName = userState?.displayName
    val profilePictureUrl = userState?.photoUrl

    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(
                accountSpecificUrl = profilePictureUrl,
                currentAccountId = userState?.uid ?: "",
                size = 80,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayName ?: "Guest User",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            if (email != null) {
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Sign in to sync your data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                if (email == null) {
                    CredentialButton(
                        onClick = onLogIn,
                        text = "Log In",
                        icon = Icons.AutoMirrored.Filled.Login,
                        modifier = Modifier.weight(1f)
                    )
                }

                CredentialButton(
                    onClick = onLogOut,
                    text = if (email != null) "Log Out" else "Logout Guest",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    containerColor = if (email != null)
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (email != null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorSettings(onColorChange: (Color) -> Unit) {
    val controller = rememberColorPickerController()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            controller = controller,
            onColorChanged = { colorEnvelope ->
                if (colorEnvelope.fromUser) {
                    onColorChange(colorEnvelope.color)
                }
            }
        )

        ColorPickerSlider(label = "Hue", controller = controller) {
            HueSlider(modifier = it, controller = controller)
        }

        ColorPickerSlider(label = "Saturation", controller = controller) {
            SaturationSlider(modifier = it, controller = controller)
        }

        ColorPickerSlider(label = "Value/Brightness", controller = controller) {
            BrightnessSlider(modifier = it, controller = controller)
        }

        ColorPickerSlider(label = "Opacity (Alpha)", controller = controller) {
            AlphaSlider(modifier = it, controller = controller)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Selected:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AlphaTile(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp)),
                controller = controller
            )
        }
    }
}

@Composable
private fun ColorPickerSlider(
    label: String,
    controller: com.github.skydoves.colorpicker.compose.ColorPickerController,
    slider: @Composable (Modifier) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        slider(Modifier
            .fillMaxWidth()
            .height(24.dp))
    }
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
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (theme == themeConfig),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = StewardTheme.colors.primary,
                    ),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = when (theme) {
                        ThemeConfig.SYSTEM -> "System Default"
                        ThemeConfig.LIGHT -> "Light Mode"
                        ThemeConfig.DARK -> "Dark Mode"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (theme == themeConfig) FontWeight.Bold else FontWeight.Normal
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
                text = "Dynamic Color",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
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
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )
    }
}
