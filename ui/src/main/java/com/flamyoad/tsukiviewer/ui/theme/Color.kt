package com.flamyoad.tsukiviewer.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// Original XML Colors (from colors.xml)
// ============================================

// Primary colors (dark gray toolbar)
val ColorPrimary = Color(0xFF313335)
val ColorPrimaryDark = Color(0xFF2B2B2B)
val ColorPrimaryLight = Color(0xFF42464B)
val ColorAccent = Color(0xFF42464B)

// Background colors
val MiWhite = Color(0xFFECECEC)
val MainBackgroundColor = MiWhite
val CardWhite = Color(0xFFFFFFFF)

// Text colors
val MainTextColor = ColorPrimaryDark
val SubTextColor = Color(0xFF959595)
val SubLightTextColor = Color(0xFFC4C4C4)
val TextWhite = Color(0xFFFFFFFF)

// Accent colors
val ActionBlue = Color(0xFF267AFF)
val SkyBlue = Color(0xFF039BE5)
val WineRed = Color(0xFFB33E5C)
val NhFavouritePink = Color(0xFFED2553)
val BlackOlive = Color(0xFF5D2A42)

// UI element colors
val SubtleGray = Color(0xFFE4E4E4)
val HeaderGray = Color(0xFF42464B)
val DeepGray = Color(0xFF8D99AE)
val NavDrawerScrim = Color(0x8D000000)
val GridItemSelectedColor = Color(0xFFE4E4E4)

// Tag colors
val TagLeftBackground = Color(0xFF666666)
val TagRightBackground = Color(0xFF323232)

// Reader mode colors
val ReadModeButtonActive = Color(0xC3B33E5C)
val ReadModeButtonInactive = Color(0xC35A5757)

// Web/misc colors
val WebDraculaBackground = Color(0xFF282A36)

// ============================================
// Material3 Color Scheme mappings
// ============================================

// Light Theme - matching the original app's light content + dark toolbar
val Primary = ColorPrimary                    // #313335 - dark gray (toolbar)
val OnPrimary = TextWhite                     // White text on toolbar
val PrimaryContainer = ColorPrimaryLight     // #42464B - slightly lighter
val OnPrimaryContainer = TextWhite

val Secondary = WineRed                       // #B33E5C - wine red accent
val OnSecondary = TextWhite
val SecondaryContainer = Color(0xFFFFD9E2)   // Light pink container
val OnSecondaryContainer = WineRed

val Tertiary = ActionBlue                     // #267AFF - blue actions
val OnTertiary = TextWhite
val TertiaryContainer = Color(0xFFD6E3FF)
val OnTertiaryContainer = ActionBlue

val Background = MiWhite                      // #ECECEC - light gray background
val OnBackground = MainTextColor              // Dark text
val Surface = CardWhite                       // #FFFFFF - white cards
val OnSurface = MainTextColor
val SurfaceVariant = SubtleGray               // #E4E4E4
val OnSurfaceVariant = SubTextColor           // #959595

val Error = Color(0xFFB00020)
val OnError = TextWhite

val Outline = SubTextColor
val OutlineVariant = SubtleGray

// Dark Theme Colors
val PrimaryDarkTheme = ColorPrimaryLight      // #42464B
val OnPrimaryDark = TextWhite
val PrimaryContainerDark = ColorPrimaryDark   // #2B2B2B
val OnPrimaryContainerDark = MiWhite

val SecondaryDarkTheme = WineRed
val OnSecondaryDark = TextWhite
val SecondaryContainerDark = Color(0xFF5D2A42)
val OnSecondaryContainerDark = Color(0xFFFFD9E2)

val TertiaryDarkTheme = SkyBlue               // #039BE5
val OnTertiaryDark = TextWhite

val BackgroundDark = Color(0xFF1A1A1A)
val OnBackgroundDark = MiWhite
val SurfaceDark = Color(0xFF2B2B2B)
val OnSurfaceDark = MiWhite
val SurfaceVariantDark = ColorPrimaryLight
val OnSurfaceVariantDark = SubLightTextColor

val ErrorDark = Color(0xFFCF6679)
val OnErrorDark = Color(0xFF000000)

val OutlineDark = DeepGray
val OutlineVariantDark = ColorPrimaryLight
