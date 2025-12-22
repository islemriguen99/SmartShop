package com.islemriguen.smartshop.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.ui.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    database: SmartShopDatabase,
    viewModel: ProductViewModel,
    onAddProductClick: () -> Unit = {},
    onViewProductsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onChatbotClick: () -> Unit = {}
) {
    val listState by viewModel.listState.collectAsState()
    val userName = FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) ?: "User"

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { PremiumWelcomeCard(userName = userName) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PremiumStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Products",
                        value = listState.totalProducts.toString(),
                        icon = Icons.Filled.Inventory2,
                        color = MaterialTheme.colorScheme.primary
                    )
                    PremiumStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Value",
                        value = "$${String.format("%.0f", listState.totalValue)}",
                        icon = Icons.Filled.TrendingUp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            item { Text("Quick Actions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
            item {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumActionButton(
                            modifier = Modifier.weight(1f),
                            text = "Products",
                            icon = Icons.Filled.Inventory2,
                            onClick = onViewProductsClick,
                            color = MaterialTheme.colorScheme.primary
                        )
                        PremiumActionButton(
                            modifier = Modifier.weight(1f),
                            text = "Search",
                            icon = Icons.Filled.Search,
                            onClick = onSearchClick,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PremiumActionButton(
                            modifier = Modifier.weight(1f),
                            text = "Chatbot",
                            icon = Icons.Filled.Chat,
                            onClick = onChatbotClick,
                            color = Color(0xFFEC4899)
                        )
                    }
                }
            }

            if (listState.products.isNotEmpty()) {
                item { Text("Recent Products", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
                items(minOf(3, listState.products.size)) { index ->
                    ModernProductCard(product = listState.products[index])
                }
            } else {
                item { EmptyStateCard(onAddClick = onAddProductClick) }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
