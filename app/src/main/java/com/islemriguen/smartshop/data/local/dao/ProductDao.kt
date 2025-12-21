// ============================================================
// FILE 3: data/local/dao/ProductDao.kt - Interface
// ============================================================
package com.islemriguen.smartshop.data.local.dao

import com.islemriguen.smartshop.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) Interface
 * Defines all database operations for products
 * Implemented by ProductDaoImpl (in-memory)
 */
interface ProductDao {

    // Create
    suspend fun insertProduct(product: ProductEntity): Long

    // Read - get all products as Flow (real-time updates)
    fun getAllProducts(): Flow<List<ProductEntity>>

    // Read - get single product by ID
    suspend fun getProductById(id: String): ProductEntity?

    // Update
    suspend fun updateProduct(product: ProductEntity)

    // Delete
    suspend fun deleteProduct(product: ProductEntity)

    // Delete by ID
    suspend fun deleteProductById(id: String)

    // Get total product count
    fun getProductCount(): Flow<Int>

    // Get total stock value
    fun getTotalStockValue(): Flow<Double?>

    // Get unsynced products (for cloud sync)
    suspend fun getUnsyncedProducts(): List<ProductEntity>
}