package com.wham.moo.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaSoft
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextSub

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "首页", Icons.Default.Home)
    object Diary : BottomNavItem("diary", "日记", Icons.Default.Book)
    object Wish : BottomNavItem("wish", "愿望", Icons.Default.Star)
    object Mind : BottomNavItem("mind", "正念", Icons.Default.Cloud)
    object Me : BottomNavItem("me", "我的", Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Diary,
    BottomNavItem.Wish,
    BottomNavItem.Mind,
    BottomNavItem.Me
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = StellaSoft,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.route == item.route
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.1f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "scale"
            )
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        val popped = navController.popBackStack(item.route, inclusive = false, saveState = false)
                        if (!popped) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(22.dp)
                                .scale(scale),
                            tint = if (selected) StellaPrimary else StellaTextSub
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) StellaPrimary else StellaTextSub
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StellaPrimary,
                    selectedTextColor = StellaPrimary,
                    indicatorColor = StellaSoft,
                    unselectedIconColor = StellaTextSub,
                    unselectedTextColor = StellaTextSub
                )
            )
        }
    }
}