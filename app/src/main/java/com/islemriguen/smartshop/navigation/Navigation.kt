// ============================================================
// ISSUE 4: Fix Navigation.kt - Proper Integration
// ============================================================
package com.islemriguen.smartshop.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.ui.auth.LoginScreen
import com.islemriguen.smartshop.ui.home.HomeScreen

@Composable
fun NavigationGraph(database: SmartShopDatabase) {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                database = database,
                navController = navController,
                onLogout = {
                    isLoggedIn = false
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
