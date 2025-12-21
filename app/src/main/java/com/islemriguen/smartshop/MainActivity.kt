// MainActivity.kt
package com.islemriguen.smartshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.islemriguen.smartshop.data.local.SmartShopDatabase
import com.islemriguen.smartshop.navigation.NavigationGraph
import com.islemriguen.smartshop.ui.theme.SmartShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        val database = SmartShopDatabase.getDatabase(this)

        setContent {
            SmartShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(database = database)
                }
            }
        }
    }
}