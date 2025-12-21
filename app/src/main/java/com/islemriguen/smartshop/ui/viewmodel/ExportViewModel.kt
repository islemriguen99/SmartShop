// ============================================================
// ui/viewmodel/ExportViewModel.kt (UPDATED)
// ============================================================
package com.islemriguen.smartshop.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islemriguen.smartshop.domain.model.Product
import com.islemriguen.smartshop.utils.ExportUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ExportUiState(
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val successMessage: String? = null,
    val exportError: String? = null,
    val lastExportTime: Long? = null,
    val lastExportFile: File? = null
)

class ExportViewModel : ViewModel() {
    private val _exportState = MutableStateFlow(ExportUiState())
    val exportState: StateFlow<ExportUiState> = _exportState

    /**
     * Export products to CSV format
     * @param share - If true, opens share dialog after export
     */
    fun exportToCSV(context: Context, products: List<Product>, share: Boolean = false) {
        viewModelScope.launch {
            _exportState.value = _exportState.value.copy(isExporting = true, exportError = null)
            try {
                val file = ExportUtils.exportToCSV(context, products)
                if (file != null) {
                    // If user wants to share, open share dialog
                    if (share) {
                        ExportUtils.shareFile(context, file)
                    }

                    _exportState.value = _exportState.value.copy(
                        isExporting = false,
                        exportSuccess = true,
                        successMessage = "CSV exported to: ${file.name}",
                        lastExportTime = System.currentTimeMillis(),
                        lastExportFile = file
                    )
                } else {
                    throw Exception("Failed to create CSV file")
                }
            } catch (e: Exception) {
                _exportState.value = _exportState.value.copy(
                    isExporting = false,
                    exportError = e.message ?: "CSV export failed"
                )
            }
        }
    }

    /**
     * Export products to Excel format
     * @param share - If true, opens share dialog after export
     */
    fun exportToExcel(context: Context, products: List<Product>, share: Boolean = false) {
        viewModelScope.launch {
            _exportState.value = _exportState.value.copy(isExporting = true, exportError = null)
            try {
                val file = ExportUtils.exportToExcel(context, products)
                if (file != null) {
                    if (share) {
                        ExportUtils.shareFile(context, file)
                    }

                    _exportState.value = _exportState.value.copy(
                        isExporting = false,
                        exportSuccess = true,
                        successMessage = "Excel exported to: ${file.name}",
                        lastExportTime = System.currentTimeMillis(),
                        lastExportFile = file
                    )
                } else {
                    throw Exception("Failed to create Excel file")
                }
            } catch (e: Exception) {
                _exportState.value = _exportState.value.copy(
                    isExporting = false,
                    exportError = e.message ?: "Excel export failed"
                )
            }
        }
    }

    /**
     * Export products to Text file
     * @param share - If true, opens share dialog after export
     */
    fun exportToText(context: Context, products: List<Product>, share: Boolean = false) {
        viewModelScope.launch {
            _exportState.value = _exportState.value.copy(isExporting = true, exportError = null)
            try {
                val file = ExportUtils.exportToTextFile(context, products)
                if (file != null) {
                    if (share) {
                        ExportUtils.shareFile(context, file)
                    }

                    _exportState.value = _exportState.value.copy(
                        isExporting = false,
                        exportSuccess = true,
                        successMessage = "Text report exported to: ${file.name}",
                        lastExportTime = System.currentTimeMillis(),
                        lastExportFile = file
                    )
                } else {
                    throw Exception("Failed to create text file")
                }
            } catch (e: Exception) {
                _exportState.value = _exportState.value.copy(
                    isExporting = false,
                    exportError = e.message ?: "Text export failed"
                )
            }
        }
    }

    /**
     * Export products to JSON format
     * @param share - If true, opens share dialog after export
     */
    fun exportToJSON(context: Context, products: List<Product>, share: Boolean = false) {
        viewModelScope.launch {
            _exportState.value = _exportState.value.copy(isExporting = true, exportError = null)
            try {
                val file = ExportUtils.exportToJSON(context, products)
                if (file != null) {
                    if (share) {
                        ExportUtils.shareFile(context, file)
                    }

                    _exportState.value = _exportState.value.copy(
                        isExporting = false,
                        exportSuccess = true,
                        successMessage = "JSON exported to: ${file.name}",
                        lastExportTime = System.currentTimeMillis(),
                        lastExportFile = file
                    )
                } else {
                    throw Exception("Failed to create JSON file")
                }
            } catch (e: Exception) {
                _exportState.value = _exportState.value.copy(
                    isExporting = false,
                    exportError = e.message ?: "JSON export failed"
                )
            }
        }
    }

    /**
     * Share the last exported file
     */
    fun shareLastFile(context: Context) {
        val file = _exportState.value.lastExportFile
        if (file != null && file.exists()) {
            ExportUtils.shareFile(context, file)
        }
    }

    /**
     * Clear export status
     */
    fun clearExportStatus() {
        _exportState.value = ExportUiState()
    }
}