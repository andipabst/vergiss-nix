package de.andicodes.vergissnix.ui.main

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val primaryLight = Color(0xffffffff)
private val primary = Color(0xfffafafa)
private val primaryDark = Color(0xffc7c7c7)
private val secondaryLight = Color(0xffFFC107)
private val secondary = Color(0xffFFA000)
private val secondaryDark = Color(0xffFF6F00)
private val success = Color(0xff81c784)
private val chipBackground = Color(0xffdddddd)

val ColorScheme = lightColors(
    primary = primary,
    primaryVariant = primaryDark,
    secondary = secondary,
    secondaryVariant = secondaryDark,
    onPrimary = Color.Black
)

@get:Composable
val Colors.success: Color
    get() = de.andicodes.vergissnix.ui.main.success