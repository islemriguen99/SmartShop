// ============================================================
// utils/ExportUtils.kt (SIMPLIFIED - No FileProvider)
// ============================================================
package com.islemriguen.smartshop.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.islemriguen.smartshop.domain.model.Product
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {
    private const val TAG = "ExportUtils"

    /**
     * Export products to CSV format
     * Saves directly to app cache directory
     */
    fun exportToCSV(context: Context, products: List<Product>): File? {
        return try {
            val fileName = "SmartShop_Products_${getCurrentDate()}.csv"
            val file = File(context.cacheDir, fileName)

            val csvContent = buildString {
                // Header row
                append("Product Name,Quantity,Unit Price,Total Value,Created Date\n")

                // Product data rows
                products.forEach { product ->
                    append("\"${product.name}\",")
                    append("${product.quantity},")
                    append("${product.price},")
                    append("${String.format("%.2f", product.getTotalValue())},")
                    append("${formatDate(product.createdAt)}\n")
                }

                // Summary section
                append("\n\n")
                append("SUMMARY\n")
                append("Total Products,${products.size}\n")
                append("Total Stock Value,${String.format("%.2f", products.sumOf { it.getTotalValue() })}\n")

                if (products.isNotEmpty()) {
                    append("Average Product Value,${String.format("%.2f", products.sumOf { it.getTotalValue() } / products.size)}\n")
                }
            }

            file.writeText(csvContent)
            Log.d(TAG, "CSV exported successfully to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to CSV: ${e.message}", e)
            null
        }
    }

    /**
     * Export products to Excel format (.xlsx simulation using CSV)
     * For true Excel, you would need Apache POI library
     * This creates a properly formatted TSV that Excel reads well
     */
    fun exportToExcel(context: Context, products: List<Product>): File? {
        return try {
            val fileName = "SmartShop_Inventory_${getCurrentDate()}.xlsx"
            val file = File(context.cacheDir, fileName)

            // Create Excel-compatible TSV (Tab-Separated Values)
            val excelContent = buildString {
                // Sheet title
                append("SMARTSHOP INVENTORY REPORT\n")
                append("Generated: ${getCurrentDateTime()}\n")
                append("\n\n")

                // Headers with formatting
                append("Product Name\tQuantity\tUnit Price\tTotal Value\tCreated Date\n")

                // Data rows
                products.forEachIndexed { index, product ->
                    append("${index + 1}. ${product.name}\t")
                    append("${product.quantity}\t")
                    append("${product.price}\t")
                    append("${String.format("%.2f", product.getTotalValue())}\t")
                    append("${formatDate(product.createdAt)}\n")
                }

                // Summary section
                append("\n\n")
                append("SUMMARY STATISTICS\t\t\t\t\n")
                append("Total Products\t${products.size}\t\t\t\n")
                append("Total Stock Value\t${String.format("%.2f", products.sumOf { it.getTotalValue() })}\t\t\t\n")

                if (products.isNotEmpty()) {
                    val avgValue = products.sumOf { it.getTotalValue() } / products.size
                    append("Average Product Value\t${String.format("%.2f", avgValue)}\t\t\t\n")
                    append("Most Expensive Item\t${products.maxByOrNull { it.price }?.name}\t${products.maxByOrNull { it.price }?.price}\t\t\n")
                    append("Lowest Stock\t${products.minByOrNull { it.quantity }?.name}\t${products.minByOrNull { it.quantity }?.quantity}\t\t\n")
                }
            }

            file.writeText(excelContent)
            Log.d(TAG, "Excel file exported successfully to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to Excel: ${e.message}", e)
            null
        }
    }

    /**
     * Export products to plain text file
     * Creates a readable text report
     */
    fun exportToTextFile(context: Context, products: List<Product>): File? {
        return try {
            val fileName = "SmartShop_Report_${getCurrentDate()}.txt"
            val file = File(context.cacheDir, fileName)

            val textContent = buildString {
                append("=" * 80 + "\n")
                append("SMARTSHOP - INVENTORY MANAGEMENT REPORT\n")
                append("=" * 80 + "\n")
                append("Generated: ${getCurrentDateTime()}\n")
                append("Total Products: ${products.size}\n")
                append("=" * 80 + "\n\n")

                if (products.isEmpty()) {
                    append("No products in inventory.\n")
                } else {
                    append("PRODUCT DETAILS:\n")
                    append("-" * 80 + "\n")

                    products.forEachIndexed { index, product ->
                        append("\n${index + 1}. ${product.name}\n")
                        append("   Quantity: ${product.quantity} units\n")
                        append("   Unit Price: \$${String.format("%.2f", product.price)}\n")
                        append("   Total Value: \$${String.format("%.2f", product.getTotalValue())}\n")
                        append("   Created: ${formatDate(product.createdAt)}\n")
                    }

                    append("\n" + "-" * 80 + "\n")
                    append("SUMMARY STATISTICS:\n")
                    append("-" * 80 + "\n")
                    append("Total Products: ${products.size}\n")
                    append("Total Stock Value: \$${String.format("%.2f", products.sumOf { it.getTotalValue() })}\n")
                    append("Average Product Value: \$${String.format("%.2f", products.sumOf { it.getTotalValue() } / products.size)}\n")
                    append("Most Expensive: ${products.maxByOrNull { it.price }?.name} (\$${products.maxByOrNull { it.price }?.price})\n")
                    append("Lowest Stock: ${products.minByOrNull { it.quantity }?.name} (${products.minByOrNull { it.quantity }?.quantity} units)\n")
                }

                append("\n" + "=" * 80 + "\n")
                append("End of Report\n")
                append("=" * 80 + "\n")
            }

            file.writeText(textContent)
            Log.d(TAG, "Text file exported successfully to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to text file: ${e.message}", e)
            null
        }
    }

    /**
     * Export products to JSON format
     * Useful for data portability and API integration
     */
    fun exportToJSON(context: Context, products: List<Product>): File? {
        return try {
            val fileName = "SmartShop_Products_${getCurrentDate()}.json"
            val file = File(context.cacheDir, fileName)

            val jsonContent = buildString {
                append("{\n")
                append("  \"exportDate\": \"${getCurrentDateTime()}\",\n")
                append("  \"totalProducts\": ${products.size},\n")
                append("  \"totalValue\": ${String.format("%.2f", products.sumOf { it.getTotalValue() })},\n")
                append("  \"products\": [\n")

                products.forEachIndexed { index, product ->
                    append("    {\n")
                    append("      \"id\": \"${product.id}\",\n")
                    append("      \"name\": \"${product.name}\",\n")
                    append("      \"quantity\": ${product.quantity},\n")
                    append("      \"price\": ${product.price},\n")
                    append("      \"totalValue\": ${String.format("%.2f", product.getTotalValue())},\n")
                    append("      \"createdAt\": \"${formatDate(product.createdAt)}\"\n")
                    append("    }")
                    if (index < products.size - 1) append(",")
                    append("\n")
                }

                append("  ]\n")
                append("}\n")
            }

            file.writeText(jsonContent)
            Log.d(TAG, "JSON file exported successfully to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to JSON: ${e.message}", e)
            null
        }
    }

    /**
     * Get all exported files from cache directory
     */
    fun getExportedFiles(context: Context): List<File> {
        return try {
            context.cacheDir.listFiles()?.filter {
                it.extension in listOf("csv", "xlsx", "txt", "json")
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting exported files: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get file path for export
     */
    fun getExportFilePath(context: Context, fileName: String): String {
        return File(context.cacheDir, fileName).absolutePath
    }

    /**
     * Delete an exported file
     */
    fun deleteExportFile(context: Context, fileName: String): Boolean {
        return try {
            val file = File(context.cacheDir, fileName)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting export file: ${e.message}")
            false
        }
    }

    /**
     * Get cache directory size
     */
    fun getCacheDirSize(context: Context): Long {
        return try {
            context.cacheDir.walk().sumOf { it.length() }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Share exported file via email, WhatsApp, Drive, etc.
     * Opens system chooser dialog for user to select app
     */
    fun shareFile(context: Context, file: File) {
        try {
            val uri = Uri.fromFile(file)
            val mimeType = when {
                file.name.endsWith(".csv") -> "text/csv"
                file.name.endsWith(".xlsx") -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                file.name.endsWith(".txt") -> "text/plain"
                file.name.endsWith(".json") -> "application/json"
                else -> "text/plain"
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_SUBJECT, "SmartShop Inventory Report - ${file.name}")
                putExtra(Intent.EXTRA_TEXT, "Please find attached the SmartShop inventory report.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(intent, "Share Inventory Report")
            )
            Log.d(TAG, "File shared: ${file.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing file: ${e.message}", e)
        }
    }

    // Helper functions
    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
    }

    private operator fun String.times(count: Int): String {
        return this.repeat(count)
    }
}