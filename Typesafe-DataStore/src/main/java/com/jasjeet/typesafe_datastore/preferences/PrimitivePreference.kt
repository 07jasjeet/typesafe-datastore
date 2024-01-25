package com.jasjeet.typesafe_datastore.preferences

/** [Preference]s which are primitive in nature can implement this interface.*/
interface PrimitivePreference<T>: Preference<T, T>