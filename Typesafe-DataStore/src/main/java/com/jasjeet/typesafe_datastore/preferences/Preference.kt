package com.jasjeet.typesafe_datastore.preferences

import kotlinx.coroutines.flow.Flow

/** A Type-safe preference.*/
interface Preference<T, R> {
    /** @return T Value of the preference.*/
    suspend fun get(): T
    
    /** @return Flow<T> Subscript-able flow to observe changes in the value of the preference.*/
    fun getFlow(): Flow<T>
    
    /** @return [Result] If the value was updated or not. Use [Result.isSuccess] or [Result.isFailure].*/
    suspend fun set(value: T): Result<Unit>
    
    /** Update the value of the preference in an atomic read-modify-write manner.
     * @return Result If the operation was successful or not. Use [Result.isSuccess] or [Result.isFailure].*/
    suspend fun getAndUpdate(update: (T) -> T): Result<Unit>
}

