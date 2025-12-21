// ============================================
//  ui/screens/ProductFormScreen.kt
// ============================================
package com.islemriguen.smartshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.islemriguen.smartshop.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    database: SmartShopDatabase,
    productId: String? = null,
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
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
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProductForEdit(productId)
        } else {
            viewModel.resetForm()
        }
    }

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (formState.isEditMode) "Edit Product" else "Add Product",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Error Message
        if (formState.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = formState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Product Name
        OutlinedTextField(
            value = formState.name,
            onValueChange = { viewModel.updateFormName(it) },
            label = { Text("Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            enabled = !formState.isLoading
        )

        // Quantity
        OutlinedTextField(
            value = formState.quantity,
            onValueChange = { viewModel.updateFormQuantity(it) },
            label = { Text("Quantity") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = !formState.isLoading
        )

        // Price
        OutlinedTextField(
            value = formState.price,
            onValueChange = { viewModel.updateFormPrice(it) },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            enabled = !formState.isLoading
        )

        // Submit Button
        Button(
            onClick = {
                if (formState.isEditMode) {
                    viewModel.updateProduct()
                } else {
                    viewModel.addProduct()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !formState.isLoading
        ) {
            if (formState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (formState.isEditMode) "Update Product" else "Add Product")
            }
        }
    }
}