package com.jasjeet.typesafe_datastore_test

import com.jasjeet.typesafe_datastore.preferences.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T, R> MockPreference(mockValue: T): Preference<T, R> =
    object: Preference<T, R> {
        override suspend fun get(): T = mockValue
        
        override fun getFlow(): Flow<T> = flow { emit(mockValue) }
        
        override suspend fun getAndUpdate(update: (T) -> T) = Result.success(Unit)
        
        override suspend fun set(value: T): Result<Unit> = Result.success(Unit)
    }
