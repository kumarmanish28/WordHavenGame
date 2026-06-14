package com.manish.wordhaven.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.manish.wordhaven.presentation.gameplay.GameplayScreen
import com.manish.wordhaven.presentation.home.HomeScreen
import com.manish.wordhaven.presentation.home.SplashScreen
import com.manish.wordhaven.presentation.levelselect.LevelSelectScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Gameplay : Screen("gameplay/{levelId}?fromMenu={fromMenu}") {
        fun createRoute(levelId: Int, fromMenu: Boolean = false) = "gameplay/$levelId?fromMenu=$fromMenu"
    }
    object LevelSelect : Screen("level_select")
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
                onPlayClick = { levelId -> navController.navigate(Screen.Gameplay.createRoute(levelId, true)) },
                onLevelSelectClick = { navController.navigate(Screen.LevelSelect.route) },
            )
        }
        composable(
            route = Screen.Gameplay.route,
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType },
                navArgument("fromMenu") { type = NavType.BoolType; defaultValue = false }
            )
        ) {
            GameplayScreen(
                onBack = { navController.popBackStack() },
                onPauseClick = { navController.popBackStack() },
                onLevelComplete = { levelId ->
                    navController.navigate(
                        Screen.Gameplay.createRoute(levelId + 1, false)
                    ) {
                        popUpTo(Screen.Gameplay.route) {
                            inclusive = true
                        }
                    }
                },
                onGameComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.LevelSelect.route) {
            LevelSelectScreen(
                onLevelSelected = { levelId ->
                    navController.navigate(Screen.Gameplay.createRoute(levelId, true))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
