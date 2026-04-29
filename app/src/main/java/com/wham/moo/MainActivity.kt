package com.wham.moo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wham.moo.ui.diary.DiaryScreen
import com.wham.moo.ui.home.HomeScreen
import com.wham.moo.ui.mind.MindScreen
import com.wham.moo.ui.navigation.BottomNavBar
import com.wham.moo.ui.profile.ProfileScreen
import com.wham.moo.ui.theme.StellaTheme
import com.wham.moo.ui.viewmodel.StellaViewModel
import com.wham.moo.ui.wish.WishScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StellaTheme {
                StellaApp()
            }
        }
    }
}

@Composable
fun StellaApp() {
    val navController = rememberNavController()
    val viewModel: StellaViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(viewModel, navController) }
            composable("diary") { DiaryScreen(viewModel) }
            composable("wish") { WishScreen(viewModel) }
            composable("mind") { MindScreen(viewModel) }
            composable("me") { ProfileScreen(viewModel) }
        }
    }
}