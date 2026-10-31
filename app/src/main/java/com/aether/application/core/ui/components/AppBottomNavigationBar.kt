package com.aether.core.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.theme.*

data class BottomBarItem(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int
)

private val HomeButtonSize = 56.dp
private val HomeOverlap = 24.dp
private val BarContentHeight = 76.dp

@Composable
fun AetherBottomBar(
    items: List<BottomBarItem>,
    homeItem: BottomBarItem,
    selectedRoute: String?,
    onItemClick: (BottomBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val half = items.size / 2
    val barShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val shadowColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.12f)

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = HomeOverlap)
                .shadow(
                    elevation = 16.dp,
                    shape = barShape,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .background(MaterialTheme.colorScheme.surface, barShape)
                .navigationBarsPadding()
                .height(BarContentHeight)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.take(half).forEach { item ->
                BottomBarTab(
                    item = item,
                    selected = item.route == selectedRoute,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.width(HomeButtonSize + 16.dp))

            items.drop(half).forEach { item ->
                BottomBarTab(
                    item = item,
                    selected = item.route == selectedRoute,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        HomeButton(
            item = homeItem,
            selected = homeItem.route == selectedRoute,
            onClick = { onItemClick(homeItem) },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun BottomBarTab(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(item.labelRes),
            style = bodySmallMedium,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun HomeButton(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val secondary = MaterialTheme.colorScheme.secondary
    val onSecondary = MaterialTheme.colorScheme.onSecondary

    Box(
        modifier = modifier
            .size(HomeButtonSize)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = secondary.copy(alpha = 0.4f),
                spotColor = secondary.copy(alpha = 0.4f)
            )
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(lerp(secondary, onSecondary, 0.25f), secondary)))
            .border(if (selected) 2.5.dp else 1.5.dp, onSecondary.copy(alpha = 0.85f), CircleShape)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = stringResource(item.labelRes),
            tint = onSecondary,
            modifier = Modifier.size(28.dp)
        )
    }
}

private val previewHome = BottomBarItem("home", R.string.nav_home, R.drawable.ic_nav_home)

private fun previewItems(@StringRes firstLabel: Int) = listOf(
    BottomBarItem("reports", firstLabel, R.drawable.ic_nav_reports),
    BottomBarItem("calculator", R.string.nav_calculator, R.drawable.ic_nav_calculator),
    BottomBarItem("aeko", R.string.nav_chatbot, R.drawable.ic_nav_chatbot),
    BottomBarItem("settings", R.string.nav_profile, R.drawable.ic_nav_profile)
)

@Preview(showBackground = true, backgroundColor = 0xFFF0F4F8, widthDp = 375)
@Composable
fun AetherBottomBarPreview() {
    AetherTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            AetherBottomBar(
                items = previewItems(R.string.nav_reports_manager),
                homeItem = previewHome,
                selectedRoute = "calculator",
                onItemClick = {}
            )
            AetherBottomBar(
                items = previewItems(R.string.nav_reports_employee),
                homeItem = previewHome,
                selectedRoute = "home",
                onItemClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B101E, widthDp = 375)
@Composable
fun AetherBottomBarDarkPreview() {
    AetherTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            AetherBottomBar(
                items = previewItems(R.string.nav_reports_manager),
                homeItem = previewHome,
                selectedRoute = "reports",
                onItemClick = {}
            )
            AetherBottomBar(
                items = previewItems(R.string.nav_reports_employee),
                homeItem = previewHome,
                selectedRoute = null,
                onItemClick = {}
            )
        }
    }
}