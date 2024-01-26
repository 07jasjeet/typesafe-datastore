package com.jasjeet.typesafe_datastore_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jasjeet.typesafe_datastore_app.ui.theme.TypesafedatastoreTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = MyAutoTypedPreferences(this)
        val listPreference = preferences.listPreference
        setContent {
            TypesafedatastoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val prefState by listPreference.getFlow().collectAsState(initial = listOf())
                    
                    LaunchedEffect(Unit) {
                        delay(1000)
                        listPreference.set(listOf("Hello", "World"))
                    }
                    
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(prefState.toString())
                    }
                }
            }
        }
    }
}


