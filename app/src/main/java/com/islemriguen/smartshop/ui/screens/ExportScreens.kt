// ============================================================
// ui/screens/ExportScreen.kt (COMPLETE WITH SHARE)
// ============================================================
package com.islemriguen.smartshop.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Download
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    products: List<Product>,
    onBackClick: () -> Unit = {},
    viewModel: ExportViewModel = viewModel()
) {
    val context = LocalContext.current
    val exportState by viewModel.exportState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Export & Share", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            // Header
            item {
                Text(
                    "Choose Export Format",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Select a format and choose to export or share directly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // CSV Export
            item {
                ExportOptionCardWithShare(
                    title = "CSV Format",
                    description = "Comma-Separated Values - Compatible with Excel, Sheets, and more",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = {
                        viewModel.exportToCSV(context, products, share = false)
                    },
                    onShare = {
                        viewModel.exportToCSV(context, products, share = true)
                    }
                )
            }

            // Excel Export
            item {
                ExportOptionCardWithShare(
                    title = "Excel Format",
                    description = "Microsoft Excel (.xlsx) - Fully formatted spreadsheet",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = {
                        viewModel.exportToExcel(context, products, share = false)
                    },
                    onShare = {
                        viewModel.exportToExcel(context, products, share = true)
                    }
                )
            }

            // Text Export
            item {
                ExportOptionCardWithShare(
                    title = "Text Report",
                    description = "Human-readable text format with summary statistics",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = {
                        viewModel.exportToText(context, products, share = false)
                    },
                    onShare = {
                        viewModel.exportToText(context, products, share = true)
                    }
                )
            }

            // JSON Export
            item {
                ExportOptionCardWithShare(
                    title = "JSON Format",
                    description = "JSON format - Perfect for data portability and APIs",
                    icon = Icons.Filled.Download,
                    shareIcon = Icons.Filled.Share,
                    isLoading = exportState.isExporting,
                    onExport = {
                        viewModel.exportToJSON(context, products, share = false)
                    },
                    onShare = {
                        viewModel.exportToJSON(context, products, share = true)
                    }
                )
            }

            // Success Message
            if (exportState.successMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.Download,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "✓ Export Successful",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    exportState.successMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Error Message
            if (exportState.exportError != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "✗ Export Failed",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    exportState.exportError ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Summary Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Export Summary",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Products to Export",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${products.size} items",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    "Total Value",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "$${String.format("%.2f", products.sumOf { it.getTotalValue() })}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Custom composable for export options with both Export and Share buttons
 */
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title and Description
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Export Button (saves to cache directory)
                Button(
                    onClick = {
                        onExport()
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            icon,
                            contentDescription = "Export",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Share Button (export + opens share dialog)
                Button(
                    onClick = {
                        onShare()
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    } else {
                        Icon(
                            shareIcon,
                            contentDescription = "Share",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}