package com.jasjeet.type_safedatastore

import com.jasjeet.typesafe_datastore.preferences.Preference

interface CustomPreference<T, R>: Preference<T, R> {
    // create new Functions
    fun something()
}