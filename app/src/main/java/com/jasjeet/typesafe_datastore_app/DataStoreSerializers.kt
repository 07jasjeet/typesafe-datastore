package com.jasjeet.typesafe_datastore_app

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jasjeet.typesafe_datastore.DataStoreSerializer

object DataStoreSerializers {
    private val gson = Gson()
    /** Serializes to String primitive.*/
    private fun <T> gsonSerializer(defaultValue: T): DataStoreSerializer<T, String> =
        object: DataStoreSerializer<T, String> {
            override fun from(value: String): T =
                gson.fromJson(
                    value,
                    object: TypeToken<T>() {}.type
                ) ?: defaultValue
            
            override fun to(value: T): String = gson.toJson(value)
            
            override fun default(): T = defaultValue
        }
    
    val stringListSerializer: DataStoreSerializer<List<String>, String>
        get() = gsonSerializer(emptyList())
}