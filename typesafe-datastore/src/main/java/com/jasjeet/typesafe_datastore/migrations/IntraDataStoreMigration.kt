package com.jasjeet.typesafe_datastore.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import com.jasjeet.typesafe_datastore.DataStoreSerializer
import com.jasjeet.typesafe_datastore.DataStoreSerializer.Companion.defaultSerializer
import com.jasjeet.typesafe_datastore.preferences.PrimitivePreference

/** Migrate a currently existing key to a new key, both of type [R]. This migration automatically
 * deletes [currentKey] after migration.
 *
 * **NOTE: Do not access DataStore inside [cleanUp], [migrate] or [serializer].**
 * @param currentKey Currently existing key in DataStore.
 * @param newKey New key to migrate the data to.
 * @param serializer Serializer that is used to convert Primitive type [R] to Complex type [T].
 * @param cleanUp **Optional** cleanup to do after migration has occurred.
 * @param migrate Migration function that receives current value [T] and should return new value [T]. Perform any transformations
 * needed to store preference with newer key. Input argument to this function will only be null if the preference with key [currentKey] hasn't been initialized.
 * @property T Object type related to both keys.
 * @property R Primitive type of both keys.*/
@JvmOverloads
fun <T, R> IntraDataMigration(
    currentKey: Preferences.Key<R>,
    newKey: Preferences.Key<R>,
    serializer: DataStoreSerializer<T, R>,
    cleanUp: suspend () -> Unit = {},
    migrate: suspend (currentValue: T?) -> T
): DataMigration<Preferences> =
    IntraDataMigration(
        currentKey,
        newKey,
        serializer,
        serializer,
        cleanUp,
        migrate
    )

/** Migrate a currently existing key [currentKey] of type [R] to a new key [newKey] of type [NR]. This migration automatically
 * deletes [currentKey] after migration.
 *
 * **NOTE: Do not access DataStore inside [cleanUp], [migrate] or [serializer].**
 * @param currentKey Currently existing key in DataStore.
 * @param newKey New key to migrate the data to.
 * @param currentPreferenceSerializer Serializer that is used to convert Preference bound with [currentKey].
 * Use [defaultSerializer] if your preference is [PrimitivePreference].
 * @param newPreferenceSerializer Serializer that will be used to convert Preference bound with [newKey].
 * Use [defaultSerializer] if your preference is [PrimitivePreference].
 * @param cleanUp **Optional** cleanup to do after migration has occurred.
 * @param migrate Migration function that receives current value [T] and should return new value [NT]. Perform any transformations
 * needed to store preference with newer key. Input argument to this function will return default if the preference with key [currentKey] hasn't been initialized.
 * @property T Object type related to current key.
 * @property R Primitive type of current key.
 * @property NT Object type related to new key.
 * @property NR Primitive type of new key.
 * */
@JvmOverloads
fun <T, R, NT, NR> IntraDataMigration(
    currentKey: Preferences.Key<R>,
    newKey: Preferences.Key<NR>,
    currentPreferenceSerializer: DataStoreSerializer<T, R>,
    newPreferenceSerializer: DataStoreSerializer<NT, NR>,
    cleanUp: suspend () -> Unit = {},
    migrate: suspend (currentValue: T?) -> NT,
): DataMigration<Preferences> =
    CustomMigration(
        currentKey,
        newKey,
        cleanUp
    ) { currentData ->
        val mutablePreferences = currentData.toMutablePreferences()
        val currentKeyValue = currentPreferenceSerializer.from(mutablePreferences.remove(currentKey))  // Clear old stale data and key.
        
        val newKeyValue = newPreferenceSerializer.to(migrate(currentKeyValue))
        mutablePreferences[newKey] = newKeyValue
        mutablePreferences.toPreferences()
    }

/** Migrate a currently existing [PrimitivePreference] with [currentKey] to a new key [newKey], both of type [R]. This migration automatically
 * deletes [currentKey] after migration.
 *
 * **NOTE: Do not access DataStore inside [cleanUp], [migrate] or [serializer].**
 * @param currentKey Currently existing key in DataStore.
 * @param newKey New key to migrate the data to.
 * @param cleanUp **Optional** cleanup to do after migration has occurred.
 * @param migrate Migration function that receives current value [R] and should return new value [R]. Perform any transformations
 * needed to store preference with newer key. Input argument to this function will only be null if the preference with key [currentKey] hasn't been initialized.
 * @property R Type of preference key.*/
@JvmOverloads
fun <R> IntraDataMigration(
    currentKey: Preferences.Key<R>,
    newKey: Preferences.Key<R>,
    cleanUp: suspend () -> Unit = {},
    migrate: suspend (currentValue: R?) -> R
): DataMigration<Preferences> =
    CustomMigration(
        currentKey,
        newKey,
        cleanUp
    ) { currentData ->
        val mutablePreferences = currentData.toMutablePreferences()
        val currentValue = mutablePreferences.remove(currentKey)  // Clear old stale data and key.
        
        val newValue = migrate(currentValue)
        mutablePreferences[newKey] = newValue
        mutablePreferences.toPreferences()
    }
    
/** Base Migration class.*/
private fun <R, NR> CustomMigration(
    currentKey: Preferences.Key<R>,
    newKey: Preferences.Key<NR>,
    cleanUp: suspend () -> Unit,
    migrationFunction: suspend (currentData: Preferences) -> Preferences
): DataMigration<Preferences> =
    object: DataMigration<Preferences> {
        override suspend fun cleanUp(): Unit = cleanUp()
        
        override suspend fun shouldMigrate(currentData: Preferences): Boolean {
            // If currentKey is deleted, then we are sure that migration took place.
            return currentData.contains(currentKey)
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




