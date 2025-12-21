// ============================================================
// FILE 4: data/local/entities/ProductEntity.kt
// ============================================================
package com.islemriguen.smartshop.data.local.entities

import java.util.UUID

/**
 * Product Entity
 * Represents a product in the database
 * Simple data class (no Room annotation needed)
 */
data class ProductEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val price: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncedWithCloud: Boolean = false
)