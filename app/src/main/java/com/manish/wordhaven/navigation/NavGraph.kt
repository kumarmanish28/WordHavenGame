package com.manish.wordhaven.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.manish.wordhaven.presentation.complete.LevelCompleteScreen
import com.manish.wordhaven.presentation.gameplay.GameplayScreen
import com.manish.wordhaven.presentation.home.HomeScreen
import com.manish.wordhaven.presentation.home.SplashScreen
import com.manish.wordhaven.presentation.levelselect.LevelSelectScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Gameplay : Screen("gameplay/{levelId}") {
        fun createRoute(levelId: Int) = "gameplay/$levelId"
    }
    object LevelSelect : Screen("level_select")
    object LevelComplete : Screen("complete/{levelId}/{coins}") {
        fun createRoute(levelId: Int, coins: Int) = "complete/$levelId/$coins"
    }
}

@Composable
fun WordHavenNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onPlayClick = { navController.navigate(Screen.Gameplay.createRoute(1)) },
                onLevelSelectClick = { navController.navigate(Screen.LevelSelect.route) },
            )
        }
        composable(
            route = Screen.Gameplay.route,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType })
        ) {
            GameplayScreen(
                onBack = { navController.popBackStack() },
                onPauseClick = { navController.popBackStack() },
                onLevelComplete = { levelId, coins ->
                    navController.navigate(Screen.LevelComplete.createRoute(levelId, coins)) {
                        popUpTo(Screen.Gameplay.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.LevelSelect.route) {
            LevelSelectScreen(
                onLevelSelected = { levelId ->
                    navController.navigate(Screen.Gameplay.createRoute(levelId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.LevelComplete.route,
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType },
                navArgument("coins") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            val coins = backStackEntry.arguments?.getInt("coins") ?: 0
            LevelCompleteScreen(
                levelId = levelId,
                coinsEarned = coins,
                onNextLevel = {
                    navController.navigate(Screen.Gameplay.createRoute(levelId + 1)) {
                        popUpTo(Screen.LevelComplete.route) { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
