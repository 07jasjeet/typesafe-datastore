package com.jasjeet.typesafe_datastore.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences

/** Migrate the value of preference mapped to [key] to a new value based on the return value of [shouldMigrate].
 *
 * Why use this function? DataStore runs migrations at app every app start, if your preference needs
 * some scheduled clean up, you can use this function but it is advised to *not* use this normally.
 * @param shouldMigrate Should return as soon as possible as this function is run at every app launch.
 * @param key Key to run in-place migration on.
 * @param migrationFunction Receives nullable current value and should return new preference value.*/
fun <R> InPlaceDataMigration(
    key: Preferences.Key<R>,
    shouldMigrate: (currentData: Preferences) -> Boolean,
    cleanUp: suspend () -> Unit = {},
    migrationFunction: suspend (currentValue: R?) -> R
): DataMigration<Preferences> =
    CustomMigration(
        key,
        key,
        cleanUp,
        shouldMigrate,
    ) { currentData ->
        val currentKeyValue = currentData[key]  // Clear old stale data and key.
        val newKeyValue = migrationFunction(currentKeyValue)
        
        val mutablePreferences = currentData.toMutablePreferences()
        mutablePreferences[key] = newKeyValue
        mutablePreferences.toPreferences()
    }