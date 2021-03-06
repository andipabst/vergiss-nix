package de.andicodes.vergissnix.ui.main

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val primaryLight = Color(0xffffffff)
private val primary = Color(0xfffafafa)
private val primaryDark = Color(0xffc7c7c7)

private val YellowLight = Color(0xffFFC107)
private val Yellow = Color(0xffFFA000)
private val YellowDark = Color(0xffFF6F00)

private val Green = Color(0xff81c784)

private val chipBackground = Color(0xffdddddd)

private val DarkGray = Color(0xFF333333)

val ColorScheme = lightColors(
    primary = Yellow,
    primaryVariant = YellowDark,
    secondary = Yellow,
    secondaryVariant = YellowDark,
    onPrimary = DarkGray
)

@get:Composable
val Colors.success: Color
    get() = Green