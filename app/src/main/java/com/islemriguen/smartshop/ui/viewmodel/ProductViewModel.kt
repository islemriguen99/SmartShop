// ProductViewModel.kt
package com.islemriguen.smartshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islemriguen.smartshop.data.repository.ProductRepository
import com.islemriguen.smartshop.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// UI State for Product List
data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalProducts: Int = 0,
    val totalValue: Double = 0.0
)

// UI State for Product Form (Add/Edit)
data class ProductFormUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val quantity: String = "",
    val price: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isEditMode: Boolean = false
)

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListUiState())
    val listState: StateFlow<ProductListUiState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(ProductFormUiState())
    val formState: StateFlow<ProductFormUiState> = _formState.asStateFlow()

    init {
        loadProducts()
        loadStatistics()
    }

    // Load all products
    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { products ->
                _listState.value = _listState.value.copy(products = products)
            }
        }
    }

    // Load statistics
    private fun loadStatistics() {
        viewModelScope.launch {
            repository.getProductCount().collect { count ->
                _listState.value = _listState.value.copy(totalProducts = count)
            }
        }

        viewModelScope.launch {
            repository.getTotalStockValue().collect { value ->
                _listState.value = _listState.value.copy(totalValue = value)
            }
        }
    }

    // Update form field - name
    fun updateFormName(name: String) {
        _formState.value = _formState.value.copy(name = name, error = null)
    }

    // Update form field - quantity
    fun updateFormQuantity(quantity: String) {
        _formState.value = _formState.value.copy(quantity = quantity, error = null)
    }

    // Update form field - price
    fun updateFormPrice(price: String) {
        _formState.value = _formState.value.copy(price = price, error = null)
    }

    // Validate form data
    private fun validateProductForm(): Boolean {
        val state = _formState.value

        when {
            state.name.isBlank() -> {
                _formState.value = _formState.value.copy(error = "Product name is required")
                return false
            }
            state.quantity.isBlank() -> {
                _formState.value = _formState.value.copy(error = "Quantity is required")
                return false
            }
            state.price.isBlank() -> {
                _formState.value = _formState.value.copy(error = "Price is required")
                return false
            }
            state.quantity.toIntOrNull() == null || state.quantity.toInt() < 0 -> {
                _formState.value = _formState.value.copy(error = "Quantity must be a positive number")
                return false
            }
            state.price.toDoubleOrNull() == null || state.price.toDouble() <= 0 -> {
                _formState.value = _formState.value.copy(error = "Price must be greater than 0")
                return false
            }
        }
        return true
    }

    // Add new product
    fun addProduct() {
        if (!validateProductForm()) return

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)

            val product = Product(
                id = _formState.value.id,
                name = _formState.value.name,
                quantity = _formState.value.quantity.toInt(),
                price = _formState.value.price.toDouble()
            )

            val result = repository.addProduct(product)
            result.onSuccess {
                _formState.value = ProductFormUiState(
                    id = UUID.randomUUID().toString(),
                    isSuccess = true
                )
            }
            result.onFailure { error ->
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to add product"
                )
            }
        }
    }

    // Update existing product
    fun updateProduct() {
        if (!validateProductForm()) return

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)

            val product = Product(
                id = _formState.value.id,
                name = _formState.value.name,
                quantity = _formState.value.quantity.toInt(),
                price = _formState.value.price.toDouble()
            )

            val result = repository.updateProduct(product)
            result.onSuccess {
                _formState.value = ProductFormUiState(
                    isSuccess = true,
                    isEditMode = false
                )
            }
            result.onFailure { error ->
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to update product"
                )
            }
        }
    }

    // Delete product
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true)
            val result = repository.deleteProduct(productId)

            result.onFailure { error ->
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to delete product"
                )
            }
        }
    }

    // Load product for editing
    fun loadProductForEdit(productId: String) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            if (product != null) {
                _formState.value = ProductFormUiState(
                    id = product.id,
                    name = product.name,
                    quantity = product.quantity.toString(),
                    price = product.price.toString(),
                    isEditMode = true
                )
            }
        }
    }

    // Reset form
    fun resetForm() {
        _formState.value = ProductFormUiState(
            id = UUID.randomUUID().toString()
        )
    }

    // Sync unsynced products
    fun syncWithCloud() {
        viewModelScope.launch {
            repository.syncUnsyncedProducts()
        }
    }
}