package com.islemriguen.smartshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.islemriguen.smartshop.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductViewModel,
    productId: String? = null,
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(productId) {
        if (productId != null) viewModel.loadProductForEdit(productId)
        else viewModel.resetForm()
    }

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) onSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (formState.isEditMode) "Edit Product" else "Add Product", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            formState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::updateFormName,
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.quantity,
                onValueChange = viewModel::updateFormQuantity,
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.price,
                onValueChange = viewModel::updateFormPrice,
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { if (formState.isEditMode) viewModel.updateProduct() else viewModel.addProduct() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !formState.isLoading
            ) {
                Text(if (formState.isEditMode) "Update Product" else "Add Product")
            }
        }
    }
}
