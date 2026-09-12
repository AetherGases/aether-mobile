package com.aether.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aether.application.R

val DMSansFontFamily = FontFamily(
    Font(R.font.dmsans_black, FontWeight.Black),
    Font(R.font.dmsans_extrabold, FontWeight.ExtraBold),
    Font(R.font.dmsans_bold, FontWeight.Bold),
    Font(R.font.dmsans_semibold, FontWeight.SemiBold),
    Font(R.font.dmsans_medium, FontWeight.Medium),
    Font(R.font.dmsans_regular, FontWeight.Normal),
    Font(R.font.dmsans_light, FontWeight.Light),
    Font(R.font.dmsans_extralight, FontWeight.ExtraLight),
    Font(R.font.dmsans_thin, FontWeight.Thin)
)

val displayLargeBold = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 52.sp
)
val displayLarge = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 52.sp
)

val displayMedium = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp
)

val titleLarge = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp
)

val titleMedium = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 17.sp
)

val titleMediumMD = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 17.sp
)

val titleSmall = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp
)
val bodyLargeMedium = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp
)
val bodyLarge = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)

val bodyMediumMd = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp
)
val bodyMedium = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
)

val bodySmall = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)

val bodySmallMedium = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp
)


val labelLarge = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp
)

val labelMedium = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp
)

val labelSmall = TextStyle(
    fontFamily = DMSansFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp
)

