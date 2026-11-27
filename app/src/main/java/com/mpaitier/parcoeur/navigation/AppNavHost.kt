package com.mpaitier.parcoeur.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mpaitier.parcoeur.features.course.presentation.screens.CourseListScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.CourseList) {
        composable<Route.CourseList> {
            CourseListScreen()
        }
    }
}
