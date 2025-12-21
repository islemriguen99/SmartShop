// ============================================================
// ISSUE 3: Fix HomeScreen.kt - Complete Implementation
// ============================================================
package com.islemriguen.smartshop.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.ui.screens.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    database: SmartShopDatabase,
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("dashboard") }
    var selectedProductId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartShop") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            when (currentScreen) {
                "dashboard" -> DashboardScreen(
                    database = database,
                    onAddProductClick = {
                        currentScreen = "form"
                        selectedProductId = null
                    },
                    onViewProductsClick = {
                        currentScreen = "products"
                    },
                    onViewStatisticsClick = {
                        currentScreen = "statistics"
                    }
                )

                "products" -> ProductListScreen(
                    database = database,
                    onAddProductClick = {
                        currentScreen = "form"
                        selectedProductId = null
                    },
                    onEditProductClick = { productId ->
                        currentScreen = "form"
                        selectedProductId = productId
                    }
                )

                "form" -> ProductFormScreen(
                    database = database,
                    productId = selectedProductId,
                    onBackClick = {
                        currentScreen = "products"
                        selectedProductId = null
                    },
                    onSuccess = {
                        currentScreen = "products"
                        selectedProductId = null
                    }
                )

                "statistics" -> StatisticsScreen(
                    database = database,
                    onBackClick = {
                        currentScreen = "dashboard"
                    }
                )

                "export" -> ExportScreen(
                    products = emptyList(),
                    onBackClick = {
                        currentScreen = "dashboard"
                    }
                )
            }
        }
    }
}