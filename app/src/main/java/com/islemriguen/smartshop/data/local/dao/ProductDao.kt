package com.islemriguen.smartshop.data.local.dao

import com.islemriguen.smartshop.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface ProductDao {
    suspend fun insertProduct(product: ProductEntity): Long
    fun getAllProducts(): Flow<List<ProductEntity>>
    suspend fun getProductById(id: String): ProductEntity?
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
    suspend fun deleteProductById(id: String)
    fun getProductCount(): Flow<Int>
    fun getTotalStockValue(): Flow<Double>

    // --- NEW ---
    suspend fun getUnsyncedProducts(): List<ProductEntity>
}

class ProductDaoImpl : ProductDao {
    private val productsState = MutableStateFlow<List<ProductEntity>>(emptyList())

    override suspend fun insertProduct(product: ProductEntity): Long {
        val current = productsState.value.toMutableList()
        current.add(product)
        productsState.value = current
        return 1L
    }

    override fun getAllProducts(): Flow<List<ProductEntity>> = productsState.asStateFlow()

    override suspend fun getProductById(id: String) =
        productsState.value.find { it.id == id }

    override suspend fun updateProduct(product: ProductEntity) {
        val list = productsState.value.toMutableList()
        val index = list.indexOfFirst { it.id == product.id }
        if (index >= 0) list[index] = product
        productsState.value = list
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        productsState.value = productsState.value.filterNot { it.id == product.id }
    }

    override suspend fun deleteProductById(id: String) {
        productsState.value = productsState.value.filterNot { it.id == id }
    }

    override fun getProductCount(): Flow<Int> = productsState.map { it.size }

    override fun getTotalStockValue(): Flow<Double> =
        productsState.map { it.sumOf { it.price * it.quantity } }

    // --- NEW ---
    override suspend fun getUnsyncedProducts(): List<ProductEntity> =
        productsState.value.filter { !it.syncedWithCloud }
}
