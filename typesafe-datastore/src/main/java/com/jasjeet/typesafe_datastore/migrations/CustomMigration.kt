package com.jasjeet.typesafe_datastore.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences

/** Base Migration class.*/
@JvmOverloads
internal fun <R, NR> CustomMigration(
    currentKey: Preferences.Key<R>,
    newKey: Preferences.Key<NR>,
    cleanUp: suspend () -> Unit = {},
    shouldMigrate: (currentData: Preferences) -> Boolean = { it.contains(currentKey) },
    migrationFunction: suspend (currentData: Preferences) -> Preferences
): DataMigration<Preferences> =
    object: DataMigration<Preferences> {
        override suspend fun cleanUp(): Unit = cleanUp()
        
        override suspend fun shouldMigrate(currentData: Preferences): Boolean {
            // If currentKey is deleted, then we are sure that migration took place.
            return shouldMigrate(currentData)
        }
        
        override suspend fun migrate(currentData: Preferences): Preferences {
            return try {
                migrationFunction(currentData)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Could not migrate preference of key ${currentKey.name} to ${newKey.name}: ${e.localizedMessage}"
                )
            }
        }
    }
