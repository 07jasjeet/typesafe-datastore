# TypeSafeDataStore
 TypeSafeDataStore is an abstraction layer on top of DataStore that provides type-safety without dealing with Proto-DataStore. Best choice if one is already working with Preferences DataStore and wants type-safety without the headache of migration that comes with Proto-DataStore.

## Basic Usage
 
Create preferences as follows:
  ```
  val key = booleanPreferencesKey("my-key")
  
  val preference: PrimitivePreference<Boolean>
          get() = createPrimitivePreference(key, false)
  ```
 
## Custom Preferences
 
  In-order to create **Custom** [DataStorePreference] you need to do some steps.
  Firstly, create a new interface as follows with your newer implementations init.
  ```
  interface CustomPreference<T, R>: Preference<T, R> {
       // create new Functions
  }
  ```
  Then, extend this class as follows:
  ```
  abstract class CustomDataStore(dataStore: DataStore<Preferences>): TypeSafeDataStore(dataStore) {
      // Optional helper function.
      fun <T, R> createCustomPreference(
          key: Preferences.Key<R>,
          serializer: DataStoreSerializer<T, R>
      ): CustomPreference<T, R> = object: CustomDataStorePreference<T, R>(key, serializer) {}
  
      // Required
      abstract inner class CustomDataStorePreference<T, R>(
          key: Preferences.Key<R>,
          serializer: DataStoreSerializer<T, R>
      ): CustomPreference<T>, DataStorePreference<T, R>(key, serializer) {
           // ... override functions.
      }
  }
  ```
  Why go all through this? Testability.
  
  Now to use the new preference, do as follows as you do with normal preferences.
  ```
  private val Context.dataStore: DataStore<Preferences> by ...
  
  class UserPreferences(context: Context): CustomDataStore(context.dataStore) {
  
       val userPref: CustomPreference<Map<String>, Int>
           get() = createCustomPreference(key, serializer)
       
       val anotherPref: CustomPreference<String, String>
           get() = createCustomPreference(anotherKey, anotherSerializer)
  }
  ```
