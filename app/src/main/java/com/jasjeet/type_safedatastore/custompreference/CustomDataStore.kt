package com.jasjeet.type_safedatastore.custompreference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jasjeet.type_safedatastore.CustomPreference
import com.jasjeet.typesafe_datastore.DataStoreSerializer
import com.jasjeet.typesafe_datastore.TypeSafeDataStore

abstract class CustomDataStore(dataStore: DataStore<Preferences>): TypeSafeDataStore(dataStore) {
    // Optional helper function.
    fun <T, R> createCustomPreference(
        key: Preferences.Key<R>,
        serializer: DataStoreSerializer<T, R>
    ): CustomPreference<T, R> = object: CustomDataStorePreference<T, R>(key, serializer) {}
    
    // Required
    abstract inner class CustomDataStorePreference<T, R>(
        key: Preferences.Key<R>,
        serializer: DataStoreSerializer<T, R>
    ): CustomPreference<T, R>, DataStorePreference<T, R>(key, serializer) {
        // ... override functions
        override fun something() {
            // You have access to dataStore here.
            print("Hello")
        }
    }
}