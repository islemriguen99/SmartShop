// ProductRepository.kt
package com.islemriguen.smartshop.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.islemriguen.smartshop.data.local.dao.ProductDao
import com.islemriguen.smartshop.data.local.entities.ProductEntity
import com.islemriguen.smartshop.domain.model.Product
import com.islemriguen.smartshop.domain.model.toDomain
import com.islemriguen.smartshop.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductRepository(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val userId: String
) {

    // Get all products as Flow
    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Add new product
    suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            // Insert to local database first
            val entity = product.toEntity().copy(syncedWithCloud = false)
            productDao.insertProduct(entity)

            // Sync to Firestore
            syncProductToCloud(product)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update existing product
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val entity = product.toEntity().copy(
                updatedAt = System.currentTimeMillis(),
                syncedWithCloud = false
            )
            productDao.updateProduct(entity)
            syncProductToCloud(product)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete product
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productDao.deleteProductById(productId)
            firestore.collection("users").document(userId)
                .collection("products").document(productId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get single product
    suspend fun getProductById(id: String): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    // Sync product to Firestore
    private suspend fun syncProductToCloud(product: Product) {
        try {
            firestore.collection("users").document(userId)
                .collection("products").document(product.id)
                .set(product)
                .await()

            // Mark as synced
            val entity = product.toEntity().copy(syncedWithCloud = true)
            productDao.updateProduct(entity)
        } catch (e: Exception) {
            // Fail silently, will retry on next operation
        }
    }

    // Get product statistics
    fun getProductCount(): Flow<Int> {
        return productDao.getProductCount()
    }

    fun getTotalStockValue(): Flow<Double> {
        return productDao.getTotalStockValue().map { it ?: 0.0 }
    }

    // Sync unsynced products from local to cloud
    suspend fun syncUnsyncedProducts(): Result<Unit> {
        return try {
            val unsyncedProducts = productDao.getUnsyncedProducts()
            unsyncedProducts.forEach { entity ->
                syncProductToCloud(entity.toDomain())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}