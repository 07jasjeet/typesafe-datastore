package com.jasjeet.typesafe_datastore.preferences

/** [Preference]s which are primitive ([Int], [String], [Float], [Boolean] [Long],
 * [Double] and **`Set<String>`**) by type should implement this interface.*/
interface PrimitivePreference<T>: Preference<T, T>