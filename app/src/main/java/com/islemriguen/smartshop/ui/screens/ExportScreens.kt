package com.islemriguen.smartshop.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islemriguen.smartshop.domain.model.Product
import com.islemriguen.smartshop.ui.viewmodel.ExportViewModel
import com.islemriguen.smartshop.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    productViewModel: ProductViewModel,
    onBackClick: () -> Unit = {},
    exportViewModel: ExportViewModel = viewModel()
) {
    val products by productViewModel.listState.collectAsState().run { derivedStateOf { this.value.products } }
    val exportState by exportViewModel.exportState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export & Share", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
            // Header
            item {
                Text("Choose Export Format", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Select a format and choose to export or share directly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // CSV
            item {
                ExportOptionCardWithShare(
                    title = "CSV Format",
                    description = "Comma-Separated Values - Compatible with Excel, Sheets, and more",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = { exportViewModel.exportToCSV(context, products, share = false) },
                    onShare = { exportViewModel.exportToCSV(context, products, share = true) }
                )
            }

            // Excel
            item {
                ExportOptionCardWithShare(
                    title = "Excel Format",
                    description = "Microsoft Excel (.xlsx) - Fully formatted spreadsheet",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = { exportViewModel.exportToExcel(context, products, share = false) },
                    onShare = { exportViewModel.exportToExcel(context, products, share = true) }
                )
            }

            // Text
            item {
                ExportOptionCardWithShare(
                    title = "Text Report",
                    description = "Human-readable text format with summary statistics",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = { exportViewModel.exportToText(context, products, share = false) },
                    onShare = { exportViewModel.exportToText(context, products, share = true) }
                )
            }

            // JSON
            item {
                ExportOptionCardWithShare(
                    title = "JSON Format",
                    description = "JSON format - Perfect for data portability and APIs",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = { exportViewModel.exportToJSON(context, products, share = false) },
                    onShare = { exportViewModel.exportToJSON(context, products, share = true) }
                )
            }

            // Export success/error messages
            if (exportState.successMessage != null) {
                item { ExportMessageCard(message = exportState.successMessage!!, isError = false) }
            }
            if (exportState.exportError != null) {
                item { ExportMessageCard(message = exportState.exportError!!, isError = true) }
            }

            // Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Export Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Products to Export", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${products.size} items", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Total Value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${String.format("%.2f", products.sumOf { it.getTotalValue() })}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ExportOptionCardWithShare(
    title: String,
    description: String,
    icon: ImageVector,
    shareIcon: ImageVector,
    isLoading: Boolean = false,
    onExport: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onExport,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else {
                        Icon(icon, contentDescription = "Export", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export", fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = onShare,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onSecondary)
                    else {
                        Icon(shareIcon, contentDescription = "Share", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun ExportMessageCard(message: String, isError: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(if (isError) Icons.Filled.Close else Icons.Filled.Check, contentDescription = null, tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
