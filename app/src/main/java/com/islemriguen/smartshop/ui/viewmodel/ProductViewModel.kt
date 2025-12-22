// FILE 5: ui/viewmodel/ProductViewModel.kt
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

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalProducts: Int = 0,
    val totalValue: Double = 0.0,
    val searchQuery: String = ""
)

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

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListUiState())
    val listState: StateFlow<ProductListUiState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(ProductFormUiState())
    val formState: StateFlow<ProductFormUiState> = _formState.asStateFlow()

    init {
        loadProducts()
        loadStatistics()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { products ->
                _listState.value = _listState.value.copy(products = products)
            }
        }
    }

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

    fun updateFormName(name: String) {
        _formState.value = _formState.value.copy(name = name, error = null)
    }

    fun updateFormQuantity(quantity: String) {
        _formState.value = _formState.value.copy(quantity = quantity, error = null)
    }

    fun updateFormPrice(price: String) {
        _formState.value = _formState.value.copy(price = price, error = null)
    }

    fun updateSearchQuery(query: String) {
        _listState.value = _listState.value.copy(searchQuery = query)
    }

    private fun validateProductForm(): Boolean {
        val state = _formState.value

        when {
            state.name.isBlank() -> {
                _formState.value = _formState.value.copy(error = "Product name required")
                return false
            }
            state.quantity.isBlank() -> {
                _formState.value = _formState.value.copy(error = "Quantity required")
                return false
            }
            state.price.isBlank() -> {
                _formState.value = _formState.value.copy(error = "Price required")
                return false
            }
            state.quantity.toIntOrNull() == null || state.quantity.toInt() < 0 -> {
                _formState.value = _formState.value.copy(error = "Quantity must be positive")
                return false
            }
            state.price.toDoubleOrNull() == null || state.price.toDouble() <= 0 -> {
                _formState.value = _formState.value.copy(error = "Price must be > 0")
                return false
            }
        }
        return true
    }


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
                // --- MODIFICATION ---
                // Utilisez .copy() pour mettre à jour l'état au lieu de le remplacer
                _formState.value = _formState.value.copy(
                    isSuccess = true,
                    isLoading = false, // Explicitement arrêter le chargement
                    name = "", // Réinitialiser les champs
                    quantity = "",
                    price = "",
                    id = UUID.randomUUID().toString() // Préparer un nouvel ID pour le prochain ajout
                )
                // --- FIN DE LA MODIFICATION ---
            }
            result.onFailure { error ->
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to add product"
                )
            }
        }
    }

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
                    error = error.message ?: "Failed to update"
                )
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true)
            val result = repository.deleteProduct(productId)

            result.onFailure { error ->
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to delete"
                )
            }
        }
    }

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

    fun resetForm() {
        _formState.value = ProductFormUiState(id = UUID.randomUUID().toString())
    }
}