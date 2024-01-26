package com.jasjeet.typesafe_datastore_test

import com.jasjeet.typesafe_datastore.preferences.PrimitivePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> MockPrimitivePreference(mockValue: T): PrimitivePreference<T> =
    object : PrimitivePreference<T> {
        override suspend fun get(): T = mockValue
        
        override fun getFlow(): Flow<T> = flow { emit(mockValue) }
        
        override suspend fun getAndUpdate(update: (T) -> T) = Result.success(Unit)
        
        override suspend fun set(value: T): Result<Unit> = Result.success(Unit)
    }