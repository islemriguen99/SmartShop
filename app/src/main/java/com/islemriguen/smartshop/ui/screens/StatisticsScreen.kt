// ============================================================
// ui/screens/StatisticsScreen.kt
// ============================================================
package com.islemriguen.smartshop.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.islemriguen.smartshop.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    database: SmartShopDatabase,
    onBackClick: () -> Unit = {},
    viewModel: ProductViewModel = remember {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val repository = ProductRepository(
            database.productDao(),
            FirebaseFirestore.getInstance(),
            userId
        )
        ProductViewModel(repository)
    }
) {
    val listState by viewModel.listState.collectAsState()
    var showExportMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics & Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showExportMenu = !showExportMenu }) {
                        Icon(Icons.Filled.Download, contentDescription = "Export")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
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
            // Summary Statistics
            item {
                Text(
                    "Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryRow(
                        label = "Total Products",
                        value = listState.totalProducts.toString()
                    )
                    SummaryRow(
                        label = "Total Stock Value",
                        value = "$${String.format("%.2f", listState.totalValue)}"
                    )
                    if (listState.products.isNotEmpty()) {
                        SummaryRow(
                            label = "Average Product Value",
                            value = "$${String.format("%.2f", listState.totalValue / listState.totalProducts)}"
                        )
                    }
                }
            }

            // Product Details
            item {
                Text(
                    "Product Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(listState.products.size) { index ->
                ProductStatisticRow(product = listState.products[index])
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProductStatisticRow(product: com.islemriguen.smartshop.domain.model.Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text(
                    "$${String.format("%.2f", product.getTotalValue())}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Corrected LinearProgressIndicator call
            LinearProgressIndicator(
                progress = (product.quantity / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Qty: ${product.quantity}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "Unit: $${product.price}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}