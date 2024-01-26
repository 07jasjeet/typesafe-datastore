package com.jasjeet.typesafe_datastore_app.custompreference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jasjeet.typesafe_datastore_app.CustomPreference
import com.jasjeet.typesafe_datastore_app.DataStoreSerializers.stringListSerializer
import com.jasjeet.typesafe_datastore.preferences.ComplexPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("custom")

class CustomPreferences(context: Context): CustomDataStore(context.dataStore) {
    companion object {
        val key = stringPreferencesKey("key")
    }
    
    val preference: ComplexPreference<List<String>>
        get() = createComplexPreference(key, stringListSerializer)
    
    // Custom Preference
    val customPreference: CustomPreference<List<String>, String>
        get() = createCustomPreference(key, stringListSerializer)
    
    fun tryCustomPref() {
        // Custom Function
        customPreference.something()
    }
}

