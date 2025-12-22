package com.islemriguen.smartshop.data.repository

import android.util.Log
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
    private val TAG = "ProductRepository"

    fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }

    suspend fun addProduct(product: Product): Result<Unit> = try {
        val entity = product.toEntity().copy(syncedWithCloud = false)
        productDao.insertProduct(entity)
        syncProductToCloud(product)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error adding product: ${e.message}")
        Result.failure(e)
    }

    suspend fun updateProduct(product: Product): Result<Unit> = try {
        val entity = product.toEntity().copy(
            updatedAt = System.currentTimeMillis(),
            syncedWithCloud = false
        )
        productDao.updateProduct(entity)
        syncProductToCloud(product)
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error updating product: ${e.message}")
        Result.failure(e)
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = try {
        productDao.deleteProductById(productId)
        firestore.collection("users")
            .document(userId)
            .collection("products")
            .document(productId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting product: ${e.message}")
        Result.failure(e)
    }

    suspend fun getProductById(id: String): Product? =
        productDao.getProductById(id)?.toDomain()

    private suspend fun syncProductToCloud(product: Product) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("products")
                .document(product.id)
                .set(product)
                .await()
            productDao.updateProduct(product.toEntity().copy(syncedWithCloud = true))
            Log.d(TAG, "Product synced: ${product.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Sync error: ${e.message}")
        }
    }

    fun getProductCount(): Flow<Int> = productDao.getProductCount()

    fun getTotalStockValue(): Flow<Double> = productDao.getTotalStockValue().map { it ?: 0.0 }

    suspend fun syncUnsyncedProducts(): Result<Unit> = try {
        val unsynced = productDao.getUnsyncedProducts()
        unsynced.forEach { syncProductToCloud(it.toDomain()) }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun searchProducts(query: String): Flow<List<Product>> =
        productDao.getAllProducts().map { entities ->
            entities.filter { it.name.contains(query, ignoreCase = true) }
                .map { it.toDomain() }
        }
}
