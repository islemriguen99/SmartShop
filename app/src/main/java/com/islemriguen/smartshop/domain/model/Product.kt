// Product.kt
package com.islemriguen.smartshop.domain.model

data class Product(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getTotalValue(): Double = price * quantity
}

// Extension function to convert between Entity and Domain Model
fun com.islemriguen.smartshop.data.local.entities.ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        quantity = quantity,
        price = price,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Product.toEntity(): com.islemriguen.smartshop.data.local.entities.ProductEntity {
    return com.islemriguen.smartshop.data.local.entities.ProductEntity(
        id = id,
        name = name,
        quantity = quantity,
        price = price,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}