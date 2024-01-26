package com.jasjeet.typesafe_datastore.preferences

/** [Preference]s which have to be converted to [String] when saved can implement this interface.
 * @see Preference*/
interface ComplexPreference<T>: Preference<T, String>