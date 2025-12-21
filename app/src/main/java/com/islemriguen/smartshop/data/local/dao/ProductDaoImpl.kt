package com.islemriguen.smartshop.data.local.dao

import com.islemriguen.smartshop.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory implementation of ProductDao
 * Stores products in memory using MutableStateFlow
 * Data persists during app session
 * Syncs to Firestore for cloud persistence
 */
class ProductDaoImpl : ProductDao {
    private val productsState = MutableStateFlow<List<ProductEntity>>(emptyList())

    override suspend fun insertProduct(product: ProductEntity): Long {
        val currentList = productsState.value.toMutableList()
        currentList.add(product)
        productsState.value = currentList
        return 1L
    }

    override fun getAllProducts(): Flow<List<ProductEntity>> {
        return productsState.asStateFlow()
    }

    override suspend fun getProductById(id: String): ProductEntity? {
        return productsState.value.find { it.id == id }
    }

    override suspend fun updateProduct(product: ProductEntity) {
        val currentList = productsState.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == product.id }
        if (index >= 0) {
            currentList[index] = product
            productsState.value = currentList
        }
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        val currentList = productsState.value.toMutableList()
        currentList.removeAll { it.id == product.id }
        productsState.value = currentList
    }

    override suspend fun deleteProductById(id: String) {
        val currentList = productsState.value.toMutableList()
        currentList.removeAll { it.id == id }
        productsState.value = currentList
    }

    override fun getProductCount(): Flow<Int> {
        return productsState.map { it.size }
    }

    override fun getTotalStockValue(): Flow<Double?> {
        return productsState.map { products ->
            if (products.isEmpty()) null
            else products.sumOf { (it.price * it.quantity).toDouble() }
        }
    }

    override suspend fun getUnsyncedProducts(): List<ProductEntity> {
        return productsState.value.filter { !it.syncedWithCloud }
    }
}