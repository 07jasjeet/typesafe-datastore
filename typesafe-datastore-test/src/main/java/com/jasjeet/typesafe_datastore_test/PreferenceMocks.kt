package com.jasjeet.typesafe_datastore_test

import com.jasjeet.typesafe_datastore.preferences.ComplexPreference
import com.jasjeet.typesafe_datastore.preferences.Preference
import com.jasjeet.typesafe_datastore.preferences.PrimitivePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object PreferenceMocks {
    fun <T> mockPrimitivePreference(mockValue: T): PrimitivePreference<T> =
        object : PrimitivePreference<T> {
            override suspend fun get(): T = mockValue
            
            override fun getFlow(): Flow<T> = flow { emit(mockValue) }
            
            override suspend fun getAndUpdate(update: (T) -> T) = Result.success(Unit)
            
            override suspend fun set(value: T): Result<Unit> = Result.success(Unit)
        }
    
    fun <T> mockComplexPreference(mockValue: T) =
        object : ComplexPreference<T> {
            override suspend fun get(): T = mockValue
            
            override fun getFlow(): Flow<T> = flow { emit(mockValue) }
            
            override suspend fun getAndUpdate(update: (T) -> T) = Result.success(Unit)
            
            override suspend fun set(value: T): Result<Unit> = Result.success(Unit)
        }
    
    fun <T, R> mockPreference(mockValue: T): Preference<T, R> =
        object: Preference<T, R> {
            override suspend fun get(): T = mockValue
            
            override fun getFlow(): Flow<T> = flow { emit(mockValue) }
            
            override suspend fun getAndUpdate(update: (T) -> T) = Result.success(Unit)
            
            override suspend fun set(value: T): Result<Unit> = Result.success(Unit)
        }
}