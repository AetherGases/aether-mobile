package com.aether.application.feature.settings.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import com.aether.core.ui.theme.*
import coil3.compose.AsyncImage
import com.aether.application.R

private val ScreenBackground = Color.White
private val CardBackground = Color.White
private val CardShadowElevation = 6.dp
private val CardShape = RoundedCornerShape(20.dp)
private val RowVerticalPadding = 16.dp
private val RowIconSize = 20.dp
private val ChevronSize = 14.dp
private val AvatarSize = 112.dp
private val HeaderHeight = 200.dp
private val HeaderMargin = 12.dp
private val HeaderShape = RoundedCornerShape(28.dp)

@Composable
fun SettingsScreen(
    userName: String,
    userRole: String,
    companyTag: String,
    avatarUrl: String?,
    reportsCount: Int,
    entryDate: String,
    notificationsEnabled: Boolean,
    emailNotificationsEnabled: Boolean,
    themeLabel: String,
    languageLabel: String,
    onNotificationsChanged: (Boolean) -> Unit,
    onEmailNotificationsChanged: (Boolean) -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onTalkToAekoClick: () -> Unit,
    onReportProblemClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(
            userName = userName,
            userRole = userRole,
            companyTag = companyTag,
            avatarUrl = avatarUrl
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            StatsCard(reportsCount = reportsCount, entryDate = entryDate)

            Spacer(Modifier.height(20.dp))

            SettingsSection(title = "Preferências") {
                SettingsToggleRow(
                    icon = R.drawable.ic_notification,
                    label = "Notificação",
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChanged
                )
                RowDivider()
                SettingsToggleRow(
                    icon = R.drawable.ic_email,
                    label = "Notificação por e-mail",
                    checked = emailNotificationsEnabled,
                    onCheckedChange = onEmailNotificationsChanged
                )
                RowDivider()
                SettingsNavigationRow(
                    icon = R.drawable.ic_theme,
                    label = "Tema",
                    value = themeLabel,
                    onClick = onThemeClick
                )
                RowDivider()
                SettingsNavigationRow(
                    icon = R.drawable.ic_language,
                    label = "Idioma",
                    value = languageLabel,
                    onClick = onLanguageClick
                )
            }

            Spacer(Modifier.height(20.dp))
            SettingsSection(title = "Suporte") {
                SettingsActionRow(
                    icon = R.drawable.ic_chatbot_profile,
                    label = "Falar com o Aeko",
                    onClick = onTalkToAekoClick,
                    tint = textSecondaryLight
                )
                RowDivider()
                SettingsActionRow(
                    icon = R.drawable.ic_report_problem,
                    label = "Reportar problema",
                    onClick = onReportProblemClick,
                    tint = textSecondaryLight
                )
                RowDivider()
                SettingsActionRow(
                    icon = R.drawable.ic_terms,
                    label = "Termos de uso",
                    onClick = onTermsOfUseClick,
                    tint = textSecondaryLight
                )
                RowDivider()
                SettingsActionRow(
                    icon = R.drawable.ic_privacy,
                    label = "Política de privacidade",
                    onClick = onPrivacyPolicyClick,
                    tint = textSecondaryLight
                )
            }

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = CardShadowElevation,
                        shape = CardShape,
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    ),
                shape = CardShape,
                color = CardBackground,
                shadowElevation = 0.dp
            ) {
                SettingsActionRow(
                    icon = R.drawable.ic_logout,
                    label = "Sair da conta",
                    onClick = onLogoutClick,
                    tint = lightRed,
                    labelColor = lightRed,
                    showChevron = false,
                    horizontalPadding = 20.dp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userRole: String,
    companyTag: String,
    avatarUrl: String?
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.bg_profile_header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeaderMargin, vertical = HeaderMargin)
                .height(HeaderHeight)
                .shadow(
                    elevation = 20.dp,
                    shape = HeaderShape,
                    ambientColor = purple500.copy(alpha = 0.4f),
                    spotColor = purple500.copy(alpha = 0.4f)
                )
                .clip(HeaderShape)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = HeaderMargin + HeaderHeight - (AvatarSize / 2)),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(AvatarSize)
                    .clip(CircleShape)
                    .border(width = 4.dp, color = Color.White, shape = CircleShape)
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(userName, style = titleLarge, color = textPrimaryLight)
        Text(userRole, style = titleMediumMD, color = textTertiaryLight)
    }

    Spacer(Modifier.height(20.dp))
}

@Composable
private fun StatsCard(reportsCount: Int, entryDate: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp,purple500.copy(alpha = 0.2f), shape = CardShape)
            .shadow(
                elevation = CardShadowElevation,
                shape = CardShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = CardShape,
        color = CardBackground,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = R.drawable.ic_reports,
                iconTint = green500,
                value = reportsCount.toString(),
                label = "Relatórios"
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(textDisabledLight)
            )

            StatItem(
                icon = R.drawable.ic_calendar,
                iconTint = green500,
                value = entryDate,
                label = "Data de entrada"
            )
        }
    }
}

@Composable
private fun StatItem(
    @DrawableRes icon: Int,
    iconTint: Color,
    value: String,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(RowIconSize)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(value, style = titleMediumMD, fontWeight = FontWeight.Bold, color = textPrimaryLight)
            Text(label, style = bodyMediumMd, color = textTertiaryLight)
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            title,
            style = titleMedium,
            color = textPrimaryLight,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CardShadowElevation,
                    shape = CardShape,
                    ambientColor = purple500.copy(alpha = 0.8f),
                    spotColor = purple500.copy(alpha = 0.6f)
                ),
            shape = CardShape,
            color = CardBackground,
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(color = textDisabledLight.copy(alpha = 0.4f))
}

@Composable
private fun SettingsToggleRow(
    @DrawableRes icon: Int,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = RowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = textSecondaryLight,
                modifier = Modifier.size(RowIconSize)
            )
            Spacer(Modifier.width(14.dp))
            Text(label, style = bodyLargeMedium, color = textSecondaryLight)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = green500,
                checkedThumbColor = backgroundLightElevated
            )
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableRow(onClick)
            .padding(vertical = RowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = textSecondaryLight,
                modifier = Modifier.size(RowIconSize)
            )
            Spacer(Modifier.width(14.dp))
            Text(label, style = bodyLargeMedium, color = textSecondaryLight)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = bodyMediumMd, color = textTertiaryLight)
            Spacer(Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = textTertiaryLight,
                modifier = Modifier.size(ChevronSize)
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit,
    tint: Color = textTertiaryLight,
    labelColor: Color = textPrimaryLight,
    showChevron: Boolean = true,
    horizontalPadding: Dp = 0.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableRow(onClick)
            .padding(vertical = RowVerticalPadding, horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(RowIconSize)
            )
            Spacer(Modifier.width(14.dp))
            Text(label, style = bodyLargeMedium, color = labelColor)
        }
        if (showChevron) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = textSecondaryLight,
                modifier = Modifier.size(ChevronSize)
            )
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 1200)
@Composable
fun SettingsScreenPreview() {
    AetherTheme {
        SettingsScreen(
            userName = "Daniel Sagaz",
            userRole = "Gerente de ESG",
            companyTag = "JBS",
            avatarUrl = null,
            reportsCount = 4539,
            entryDate = "24/02/23",
            notificationsEnabled = true,
            emailNotificationsEnabled = true,
            themeLabel = "Claro",
            languageLabel = "Português (BR)",
            onNotificationsChanged = {},
            onEmailNotificationsChanged = {},
            onThemeClick = {},
            onLanguageClick = {},
            onTalkToAekoClick = {},
            onReportProblemClick = {},
            onTermsOfUseClick = {},
            onPrivacyPolicyClick = {},
            onLogoutClick = {}
        )
    }
}