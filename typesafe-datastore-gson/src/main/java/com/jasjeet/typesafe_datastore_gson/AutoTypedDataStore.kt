package com.jasjeet.typesafe_datastore_gson

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jasjeet.typesafe_datastore.DataStoreSerializer
import com.jasjeet.typesafe_datastore.TypeSafeDataStore
import com.jasjeet.typesafe_datastore.preferences.ComplexPreference
import com.jasjeet.typesafe_datastore.preferences.PrimitivePreference

/** Auto-typed datastore with [Gson] for serialization-deserialization and [TypeSafeDataStore] under the hood.
 * ***Usage:***
 * Firstly, inherit from [AutoTypedDataStore] as shown.
 * ```
 * class MyPreferences(context: Context): AutoTypedDataStore(context.dataStore) {
 *      // Your Preferences here
 * }
 * ```
 * And then, simply start creating your preferences inside your `MyPreferences` class.
 * ```
 * val key = stringPreferencesKey("my-key")
 *
 * val preference: ComplexPreference<List<String>>
 *     get() = createListPreference(key)
 * ```
 * To create custom preferences, use [createCustomPreference] and if you need more customisability, see [TypeSafeDataStore].
 * @see TypeSafeDataStore
 */
abstract class AutoTypedDataStore(dataStore: DataStore<Preferences>): TypeSafeDataStore(dataStore){
    /** Short-hand option to create [Boolean] preference.*/
    protected fun createBooleanPreference(
        key: Preferences.Key<Boolean>,
        default: Boolean = false
    ): PrimitivePreference<Boolean> = createPrimitivePreference(key, default)
    
    /** Short-hand option to create [Int] preference.*/
    protected fun createIntPreference(
        key: Preferences.Key<Int>,
        default: Int = 0
    ): PrimitivePreference<Int> = createPrimitivePreference(key, default)
    
    /** Short-hand option to create [Boolean] preference.*/
    protected fun createLongPreference(
        key: Preferences.Key<Long>,
        default: Long = 0
    ): PrimitivePreference<Long> = createPrimitivePreference(key, default)
    
    /** Short-hand option to create [Float] preference.*/
    protected fun createFloatPreference(
        key: Preferences.Key<Float>,
        default: Float = 0.0f
    ): PrimitivePreference<Float> = createPrimitivePreference(key, default)
    
    /** Short-hand option to create [Double] preference.*/
    protected fun createDoublePreference(
        key: Preferences.Key<Double>,
        default: Double = 0.0
    ): PrimitivePreference<Double> = createPrimitivePreference(key, default)
    
    /** Short-hand option to create `Set<String>` preference.*/
    protected fun createStringSetPreference(
        key: Preferences.Key<Set<String>>,
        defaultValue: Set<String>
    ): PrimitivePreference<Set<String>> = createPrimitivePreference(key, defaultValue)
    
    /** Gson-backed short-hand option to create [List] preferences.*/
    protected fun <T> createListPreference(
        key: Preferences.Key<String>,
        defaultValue: List<T> = emptyList()
    ): ComplexPreference<List<T>> = createComplexPreference(key, listSerializer(defaultValue))
    
    /** Gson-backed short-hand option to create [Map] preferences.*/
    protected fun <K, V> createMapPreference(
        key: Preferences.Key<String>,
        defaultValue: Map<K, V> = emptyMap()
    ): ComplexPreference<Map<K, V>> = createComplexPreference(key, mapSerializer(defaultValue))
    
    /** Gson-backed short-hand option to create [Array] preferences.*/
    protected inline fun <reified T> createArrayPreference(
        key: Preferences.Key<String>,
        defaultValue: Array<T> = emptyArray()
    ): ComplexPreference<Array<T>> = createComplexPreference(key, arraySerializer(defaultValue))
    
    /** Gson-backed short-hand option to create [ArrayList] preferences.*/
    protected fun <T> createArrayListPreference(
        key: Preferences.Key<String>,
        defaultValue: ArrayList<T> = arrayListOf()
    ): ComplexPreference<ArrayList<T>> = createComplexPreference(key, arrayListSerializer(defaultValue))
    
    /** Gson-backed short-hand option to create [Set] preferences.*/
    protected fun <T> createSetPreference(
        key: Preferences.Key<String>,
        defaultValue: Set<T> = setOf()
    ): ComplexPreference<Set<T>> = createComplexPreference(key, setSerializer(defaultValue))
    
    /** Gson-backed short-hand option to create [Class] preferences.*/
    protected fun <T> createCustomPreference(
        key: Preferences.Key<String>,
        defaultValue: T
    ): ComplexPreference<T> = createComplexPreference(key, gsonSerializer(defaultValue))
    
    companion object {
        private val gson = Gson()
        /** Serializes to String primitive.*/
        fun <T> gsonSerializer(defaultValue: T): DataStoreSerializer<T, String> =
            object: DataStoreSerializer<T, String> {
                override fun from(value: String): T =
                    gson.fromJson(
                        value,
                        object: TypeToken<T>() {}.type
                    ) ?: defaultValue
                
                override fun to(value: T): String = gson.toJson(value)
                
                override fun default(): T = defaultValue
            }
        
        inline fun <reified T> arraySerializer(default: Array<T> = emptyArray()) = gsonSerializer(default)
        
        fun <T> arrayListSerializer(default: ArrayList<T> = arrayListOf()) = gsonSerializer(default)
        
        fun <T> listSerializer(default: List<T> = emptyList()) = gsonSerializer(default)
        
        fun <T> setSerializer(default: Set<T> = emptySet()) = gsonSerializer(default)
        
        fun <K, V> mapSerializer(default: Map<K, V> = emptyMap()) = gsonSerializer(default)
    }
}