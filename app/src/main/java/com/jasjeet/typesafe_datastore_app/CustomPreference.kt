package com.jasjeet.typesafe_datastore_app

import com.jasjeet.typesafe_datastore.preferences.Preference

interface CustomPreference<T, R>: Preference<T, R> {
    // create new Functions
    fun something()
}