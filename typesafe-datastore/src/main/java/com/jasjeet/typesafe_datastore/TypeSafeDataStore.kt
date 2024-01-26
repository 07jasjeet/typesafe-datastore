package com.jasjeet.typesafe_datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.jasjeet.typesafe_datastore.DataStoreSerializer.Companion.defaultSerializer
import com.jasjeet.typesafe_datastore.TypeSafeDataStore.DataStorePreference
import com.jasjeet.typesafe_datastore.preferences.ComplexPreference
import com.jasjeet.typesafe_datastore.preferences.Preference
import com.jasjeet.typesafe_datastore.preferences.PrimitivePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

/** [TypeSafeDataStore] is an abstraction layer on top of [DataStore] that provides type-safety
 * without dealing with Proto-DataStore. Best choice if one is already working with 
 * [Preferences] [DataStore] and want type-safety without the headache of migration.
 *
 * ***Basic Usage***
 *
 * Create preferences as follows:
 * ```
 * val key = booleanPreferencesKey("my-key")
 * 
 * val preference: PrimitivePreference<Boolean>
 *         get() = createPrimitivePreference(key, false)
 * ```
 *
 * ***Custom Preferences***
 *
 * In-order to create **Custom** [DataStorePreference] you need to do some steps.
 * Firstly, create a new interface as follows with your newer implementations init.
 * ```
 * interface CustomPreference<T, R>: Preference<T, R> {
 *      // create new Functions
 * }
 * ```
 * Then, extend this class as follows:
 * ```
 * abstract class UserDataStore(dataStore: DataStore<Preferences>): TypeSafeDataStore(dataStore) {
 *     // Optional helper function.
 *     fun <T, R> createCustomPreference(
 *         key: Preferences.Key<R>,
 *         serializer: DataStoreSerializer<T, R>
 *     ): CustomPreference<T, R> = object: MyDataStorePreference<T, R>(key, serializer) {}
 * 
 *     // Required
 *     abstract inner class MyDataStorePreference<T, R>(
 *         key: Preferences.Key<R>,
 *         serializer: DataStoreSerializer<T, R>
 *     ): CustomPreference<T>, DataStorePreference<T, R>(key, serializer) {
 *          // ... override functions.
 *     }
 * }
 * ```
 * Why go all through this? Testability.
 * 
 * Now to use the new preference, do as follows as you do with normal preferences.
 * ```
 * private val Context.dataStore: DataStore<Preferences> by ...
 * 
 * class UserPreferences(context: Context): UserDataStore(context.dataStore) {
 * 
 *      val userPref: CustomPreference<Map<String>, Int>
 *          get() = createCustomPreference(key, serializer)
 *      
 *      val anotherPref: CustomPreference<String, String>
 *          get() = createCustomPreference(anotherKey, anotherSerializer)
 * }
 * ```
 */
abstract class TypeSafeDataStore(private val dataStore: DataStore<Preferences>) {
    /** Create a [PrimitivePreference] object.*/
    fun <T> createPrimitivePreference(
        key: Preferences.Key<T>,
        defaultValue: T
    ): PrimitivePreference<T> =
        object: PrimitiveDataStorePreference<T>(key = key, defaultValue = defaultValue) {}
    
    
    /** Create a [ComplexPreference] object.*/
    fun <T> createComplexPreference(
        key: Preferences.Key<String>,
        serializer: DataStoreSerializer<T, String>
    ): ComplexPreference<T> = object: ComplexDataStorePreference<T>(key, serializer) {}
    
    
    /** Create a [Preference] object.*/
    fun <T, R> createPreference(
        key: Preferences.Key<R>,
        serializer: DataStoreSerializer<T, R>
    ): Preference<T, R> = object: DataStorePreference<T, R>(key, serializer) {}
    
    
    /** [DataStorePreference]s which are primitive in nature can use this class.*/
    internal abstract inner class PrimitiveDataStorePreference<T>(
        key: Preferences.Key<T>,
        defaultValue: T
    ): PrimitivePreference<T>, DataStorePreference<T, T>(key, defaultSerializer(defaultValue))
    
    
    /** [DataStorePreference]s which are complex in nature can use this class.*/
    internal abstract inner class ComplexDataStorePreference<T>(
        key: Preferences.Key<String>,
        serializer: DataStoreSerializer<T, String>
    ): ComplexPreference<T>, DataStorePreference<T, String>(key, serializer)
    
    
    /** A [DataStore] preference can be declared type-safe by making an object of this class.
     *
     * Every function can be overridden.*/
    abstract inner class DataStorePreference<T, R>(
        private val key: Preferences.Key<R>,
        private val serializer: DataStoreSerializer<T, R>
    ): Preference<T, R> {
        override suspend fun get(): T = getFlow().firstOrNull() ?: serializer.default()
        
        override fun getFlow(): Flow<T> =
            dataStore.data.map { prefs ->
                prefs[key]?.let { serializer.from(it) } ?: serializer.default()
            }.catch {
                if (it is IOException)
                    emit(serializer.default())
                else
                    throw it
            }
        
        override suspend fun set(value: T): Result<Unit> =
            try {
                dataStore.edit { it[key] = serializer.to(value) }
                Result.success(Unit)
            } catch (e: IOException) {
                Result.failure(e)
            }
        
        override suspend fun getAndUpdate(update: (T) -> T): Result<Unit> {
            return try {
                dataStore.updateData { prefs ->
                    val mutablePrefs = prefs.toMutablePreferences()
                    val currentValue = prefs[key]?.let { serializer.from(it) } ?: serializer.default()
                    mutablePrefs[key] = serializer.to(update(currentValue))
                    return@updateData mutablePrefs
                }
                Result.success(Unit)
            } catch (e: IOException) {
                Result.failure(e)
            }
        }
    }
}