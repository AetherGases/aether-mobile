package com.aether.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.application.R
import com.aether.core.ui.theme.AetherTheme
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star

data class BottomNavItemData<T>(
    val route: T,
    val label: String,
    val iconRes: Int? = null,
    val iconVector: ImageVector? = null
)

@Composable
fun <T> AppBottomNavigationBar(
    leftItems: List<BottomNavItemData<T>>,
    rightItems: List<BottomNavItemData<T>>,
    centerItem: BottomNavItemData<T>,
    currentRoute: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White.copy(alpha = 0.92f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftItems.forEach { item ->
                    BottomNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onItemSelected(item.route) }
                    )
                }
                Spacer(modifier = Modifier.width(64.dp))
                rightItems.forEach { item ->
                    BottomNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onItemSelected(item.route) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp)
                .size(64.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF34D399).copy(alpha = 0.4f)
                )
                .clip(CircleShape)
                .background(Color(0xFF34D399))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onItemSelected(centerItem.route) },
            contentAlignment = Alignment.Center
        ) {
            RenderNavItemIcon(
                item = centerItem,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun <T> BottomNavItem(
    item: BottomNavItemData<T>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF34D399)
    val inactiveColor = Color(0xFF94A3B8)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 4.dp)
    ) {
        RenderNavItemIcon(
            item = item,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            color = if (isSelected) activeColor else inactiveColor,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun <T> RenderNavItemIcon(
    item: BottomNavItemData<T>,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        // Ícones nativos do Material 3 exclusivos para o Preview (evita o Render Issue)
        Icon(
            imageVector = item.iconVector ?: Icons.Outlined.Star,
            contentDescription = item.label,
            tint = tint,
            modifier = modifier
        )
    } else {
        if (item.iconRes != null) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                tint = tint,
                modifier = modifier
            )
        } else if (item.iconVector != null) {
            Icon(
                imageVector = item.iconVector,
                contentDescription = item.label,
                tint = tint,
                modifier = modifier
            )
        }
    }
}

//      FUNCIONÁRIO
enum class EmployeeTab { CREATION, CALCULATOR, HOME, CHATBOT, PROFILE }

@Composable
fun EmployeeBottomNavigationBar(
    currentTab: EmployeeTab,
    onTabSelected: (EmployeeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    AppBottomNavigationBar(
        leftItems = listOf(
            BottomNavItemData(
                route = EmployeeTab.CREATION,
                label = "Criação",
                iconRes = R.drawable.ic_report_creation_bottom
            ),
            BottomNavItemData(
                route = EmployeeTab.CALCULATOR,
                label = "Calculadora",
                iconRes = R.drawable.ic_calculator_bottom
            )
        ),
        rightItems = listOf(
            BottomNavItemData(
                route = EmployeeTab.CHATBOT,
                label = "Chatbot",
                iconRes = R.drawable.ic_chatbot
            ),
            BottomNavItemData(
                route = EmployeeTab.PROFILE,
                label = "Perfil",
                iconRes = R.drawable.ic_profile
            )
        ),
        centerItem = BottomNavItemData(
            route = EmployeeTab.HOME,
            label = "Home",
            iconRes = R.drawable.ic_home
        ),
        currentRoute = currentTab,
        onItemSelected = onTabSelected,
        modifier = modifier
    )
}

//      GERENTE
enum class ManagerTab { REPORTS, CALCULATOR, HOME, CHATBOT, PROFILE }

@Composable
fun ManagerBottomNavigationBar(
    currentTab: ManagerTab,
    onTabSelected: (ManagerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    AppBottomNavigationBar(
        leftItems = listOf(
            BottomNavItemData(
                route = ManagerTab.REPORTS,
                label = "Relatórios",
                iconRes = R.drawable.ic_report_creation_bottom
            ),
            BottomNavItemData(
                route = ManagerTab.CALCULATOR,
                label = "Calculadora",
                iconRes = R.drawable.ic_calculator_bottom
            )
        ),
        rightItems = listOf(
            BottomNavItemData(
                route = ManagerTab.CHATBOT,
                label = "Chatbot",
                iconRes = R.drawable.ic_chatbot
            ),
            BottomNavItemData(
                route = ManagerTab.PROFILE,
                label = "Perfil",
                iconRes = R.drawable.ic_profile
            )
        ),
        centerItem = BottomNavItemData(
            route = ManagerTab.HOME,
            label = "Home",
            iconRes = R.drawable.ic_home
        ),
        currentRoute = currentTab,
        onItemSelected = onTabSelected,
        modifier = modifier
    )

}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun EmployeeBottomNavigationBarPreview() {
    AetherTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            EmployeeBottomNavigationBar(
                currentTab = EmployeeTab.HOME,
                onTabSelected = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun ManagerBottomNavigationBarPreview() {
    AetherTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ManagerBottomNavigationBar(
                currentTab = ManagerTab.HOME,
                onTabSelected = {}
            )
        }
    }
}
