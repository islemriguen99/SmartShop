// ============================================================
// utils/Constants.kt
// ============================================================
package com.islemriguen.smartshop.utils

object Constants {
    // Firebase Collections
    const val FIRESTORE_USERS_COLLECTION = "users"
    const val FIRESTORE_PRODUCTS_COLLECTION = "products"

    // Validation Constants
    const val MIN_PRODUCT_NAME_LENGTH = 1
    const val MAX_PRODUCT_NAME_LENGTH = 100
    const val MIN_PRICE = 0.01
    const val MIN_QUANTITY = 0
    const val MAX_QUANTITY = 999999

    // Date Formats
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    // File Export
    const val EXPORT_DIR = "SmartShop_Exports"
    const val CSV_MIME_TYPE = "text/csv"
    const val PDF_MIME_TYPE = "application/pdf"

    // UI Constants
    const val ANIMATION_DURATION = 300
    const val DEBOUNCE_DELAY = 300L
}