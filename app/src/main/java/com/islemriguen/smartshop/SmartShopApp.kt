// SmartShopApp.kt
package com.islemriguen.smartshop

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.initialize

/**
 * SmartShop Application Class
 *
 * This class is the entry point for the entire application.
 * It initializes Firebase and other global app configurations.
 *
 * Benefits:
 * - Application-wide initialization
 * - Firebase setup (Auth, Firestore, Analytics)
 * - Global app lifecycle management
 * - Crash reporting setup
 */
class SmartShopApp : Application() {

    companion object {
        private const val TAG = "SmartShopApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        initializeFirebase()

        // Log app startup
        Log.d(TAG, "SmartShop App Started Successfully")
    }

    /**
     * Initialize Firebase services
     * - Authentication
     * - Firestore Database
     * - Analytics
     */
    private fun initializeFirebase() {
        try {
            // Firebase initializes automatically via FirebaseInitProvider
            // But we can do additional configuration here

            // Enable Firestore offline persistence
            val firestore = Firebase.firestore
            firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

            // Get Firebase Auth instance (for verification)
            val auth = Firebase.auth
            Log.d(TAG, "Firebase Auth initialized: ${auth.currentUser?.email ?: "No user logged in"}")

            // Initialize Analytics
            val analytics = Firebase.analytics
            Log.d(TAG, "Firebase Analytics initialized")

            Log.d(TAG, "Firebase initialization completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
        }
    }
}