package com.wham.moo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wham.moo.ui.diary.DiaryDetailScreen
import com.wham.moo.ui.diary.DiaryScreen
import com.wham.moo.ui.home.HomeScreen
import com.wham.moo.ui.mind.MindScreen
import com.wham.moo.ui.navigation.BottomNavBar
import com.wham.moo.ui.profile.ProfileScreen
import com.wham.moo.ui.theme.StellaTheme
import com.wham.moo.ui.viewmodel.StellaViewModel
import com.wham.moo.ui.wish.WishScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StellaViewModel = viewModel()
            val darkMode by viewModel.darkMode.collectAsState()
            StellaTheme(darkTheme = darkMode) {
                StellaApp(viewModel)
            }
        }
    }
}

@Composable
fun StellaApp(viewModel: StellaViewModel) {
    val navController = rememberNavController()

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
            composable("diary") { DiaryScreen(viewModel, navController) }
            composable(
                route = "diary_detail/{diaryId}/{diaryContent}/{diaryMood}/{diaryTime}/{diaryDate}/{diaryImageUris}",
                arguments = listOf(
                    navArgument("diaryId") { type = NavType.LongType },
                    navArgument("diaryContent") { type = NavType.StringType },
                    navArgument("diaryMood") { type = NavType.StringType },
                    navArgument("diaryTime") { type = NavType.StringType },
                    navArgument("diaryDate") { type = NavType.StringType },
                    navArgument("diaryImageUris") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("diaryId") ?: 0L
                val content = backStackEntry.arguments?.getString("diaryContent") ?: ""
                val mood = backStackEntry.arguments?.getString("diaryMood") ?: ""
                val time = backStackEntry.arguments?.getString("diaryTime") ?: ""
                val date = backStackEntry.arguments?.getString("diaryDate") ?: ""
                val imageUris = backStackEntry.arguments?.getString("diaryImageUris") ?: ""
                DiaryDetailScreen(
                    navController = navController,
                    diaryId = id,
                    diaryContent = content,
                    diaryMood = mood,
                    diaryTime = time,
                    diaryDate = date,
                    diaryImageUris = imageUris,
                    onDelete = { viewModel.deleteDiary(it) }
                )
            }
            composable("wish") { WishScreen(viewModel) }
            composable("mind") { MindScreen(viewModel) }
            composable("me") { ProfileScreen(viewModel) }
        }
    }
}