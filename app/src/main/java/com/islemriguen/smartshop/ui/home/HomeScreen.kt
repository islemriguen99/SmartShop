package com.islemriguen.smartshop.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.padding
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.ui.screens.*
import com.islemriguen.smartshop.ui.viewmodel.*
import com.islemriguen.smartshop.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    database: SmartShopDatabase,
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("dashboard") }
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // --- ViewModels ---
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val productRepository = remember { ProductRepository(database.productDao(), FirebaseFirestore.getInstance(), userId) }
    val productViewModel = remember { ProductViewModel(productRepository) }
    val exportViewModel = remember { ExportViewModel() }
    val chatViewModel = remember { ChatViewModel() }

    // --- Logout Dialog ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                    showLogoutDialog = false
                }) { Text("Yes, Logout") }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- Scaffold & TopBar ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartShop", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                "dashboard" -> DashboardScreen(
                    database = database,
                    viewModel = productViewModel,
                    onAddProductClick = { currentScreen = "form"; selectedProductId = null },
                    onViewProductsClick = { currentScreen = "products" },
                    onSearchClick = { currentScreen = "search" },
                    onChatbotClick = { currentScreen = "chatbot" }
                )

                "products" -> ProductListScreen(
                    viewModel = productViewModel,
                    onAddProductClick = { currentScreen = "form"; selectedProductId = null },
                    onEditProductClick = { productId -> currentScreen = "form"; selectedProductId = productId },
                    onBackClick = { currentScreen = "dashboard" }
                )

                "form" -> ProductFormScreen(
                    viewModel = productViewModel,
                    productId = selectedProductId,
                    onBackClick = { currentScreen = "products"; selectedProductId = null },
                    onSuccess = { currentScreen = "products"; selectedProductId = null }
                )

                "search" -> SearchScreen(
                    viewModel = productViewModel,
                    onBackClick = { currentScreen = "dashboard" }
                )

                "chatbot" -> ChatbotScreen(
                    viewModel = chatViewModel,
                    onBackClick = { currentScreen = "dashboard" }
                )

                "export" -> ExportScreen(
                    productViewModel = productViewModel,
                    exportViewModel = exportViewModel,
                    onBackClick = { currentScreen = "dashboard" }
                )

                "statistics" -> StatisticsScreen(
                    viewModel = productViewModel,
                    onBackClick = { currentScreen = "dashboard" }
                )
            }
        }
    }
}
