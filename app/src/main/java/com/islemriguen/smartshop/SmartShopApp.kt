package com.islemriguen.smartshop

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SmartShopApp : Application() {

    companion object {
        private const val TAG = "SmartShopApp"
    }

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        Log.d(TAG, "SmartShop App Started Successfully")
    }

    private fun initializeFirebase() {
        try {
            val firestore = Firebase.firestore
            firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

            val auth = Firebase.auth
            Log.d(TAG, "Firebase Auth initialized: ${auth.currentUser?.email ?: "No user"}")

            val analytics = Firebase.analytics
            Log.d(TAG, "Firebase Analytics initialized")
            Log.d(TAG, "Firebase initialization completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
        }
    }
}