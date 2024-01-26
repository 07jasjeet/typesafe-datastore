package com.jasjeet.typesafe_datastore

/** @property T Object or higher type.
 *  @property R Primitive or serializable type.*/
interface DataStoreSerializer<T, R> {
    /** Convert from Primitive [R] to Object [T].*/
    fun from(value: R): T
    
    /** Convert to Primitive [T] from Object [R].*/
    fun to(value: T): R
    
    /** Default value for errors and null values.*/
    fun default(): T
    
    companion object {
        /** Default Serializer if T and R are same in [DataStoreSerializer].*/
        fun <T> defaultSerializer(defaultValue: T): DataStoreSerializer<T, T> =
            object: DataStoreSerializer<T, T> {
                override fun from(value: T): T = value
                override fun to(value: T): T = value
                override fun default(): T = defaultValue
            }
    }
}