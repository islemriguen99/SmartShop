package com.islemriguen.smartshop.data.local

import android.content.Context
import com.islemriguen.smartshop.data.local.dao.ProductDao
import com.islemriguen.smartshop.data.local.dao.ProductDaoImpl

/**
 * SmartShop Database
 * Simplified implementation without Room
 * Uses in-memory storage with ProductDaoImpl
 */
abstract class SmartShopDatabase {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: SmartShopDatabase? = null

        fun getDatabase(context: Context): SmartShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = object : SmartShopDatabase() {
                    override fun productDao(): ProductDao = ProductDaoImpl()
                }
                INSTANCE = instance
                instance
            }
        }
    }
}