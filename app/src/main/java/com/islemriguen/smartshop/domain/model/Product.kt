package com.islemriguen.smartshop.domain.model

import com.islemriguen.smartshop.data.local.entities.ProductEntity

data class Product(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncedWithCloud: Boolean = false // ✅ added
) {
    fun getTotalValue(): Double = price * quantity
}

// Mapper from Entity to Domain
fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    quantity = quantity,
    price = price,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncedWithCloud = syncedWithCloud // ✅ map this field
)

// Mapper from Domain to Entity
fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    quantity = quantity,
    price = price,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncedWithCloud = syncedWithCloud // ✅ map this field
)
