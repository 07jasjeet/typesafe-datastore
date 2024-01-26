# TypeSafe-DataStore
 TypeSafeDataStore is a lightweight abstraction layer on top of SharedPreferences DataStore that provides type-safety without dealing with Proto-DataStore. 
  
  Why TypeSafe-DataStore?
  1. Proto-DataStore is complex, requires plugins and simply overkill for SharedPreferences. Prefer using Room if performance comes into play.
  2. If one is already using Preferences DataStore but wants type-safety, migrating to Proto-DataStore would be a lot of pain in a live app since Proto-DataStore uses Protocol Buffers under the hood. Whereas, this implementation, provides flexibility and easy of migration on a live app.
  3. Apart from type-safety, various migration functions have been provided to migrate data inside data-store in a type-safe way.
  4. Testing is really easy.

## Gradle Dependency
 Add the JitPack repository to your project's build.gradle file
 ```gradle
 allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
 }
 ```
Add the dependency in your app's build.gradle file
```gradle
dependencies {
    // Type-safe datastore
    implementation("com.github.07jasjeet:typesafe-datastore:1.0")
    // Alternatively - Gson backed type-safe datastore
    implementation("com.github.07jasjeet:typesafe-datastore-gson:1.0")   

    // Testing
    testImplementation("com.github.07jasjeet:typesafe-datastore-test:1.0")
}
```
## Development

- Prerequisite: Latest version of the Android Studio and SDKs on your pc.
- Clone this repository.
- Use the `gradlew build` command to build the project directly or use the IDE to run the project to your phone or the emulator.

## Basic Usage
 
Create preferences as follows:
  ```kotlin
  // Create your DataStore
  private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")

  // Your preferences class in the same file
  class MyPreferences(context: Context): TypeSafeDataStore(context.dataStore)
      companion object {
           val key = booleanPreferencesKey("my-key")
      }
  
      val preference: PrimitivePreference<Boolean>
          get() = createPrimitivePreference(key, false)
  ```
 
## Custom Preferences
 
  In-order to create **Custom** [DataStorePreference] you need to do some steps.
  Firstly, create a new interface as follows with your newer implementation in it.
  ```kotlin
  interface CustomPreference<T, R>: Preference<T, R> {
       // create new Functions
  }
  ```
  Then, extend this class as follows:
  ```kotlin
  abstract class CustomDataStore(dataStore: DataStore<Preferences>): TypeSafeDataStore(dataStore) {
      /* Required */
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
  ```kotlin
  private val Context.dataStore: DataStore<Preferences> by ...
  
  class UserPreferences(context: Context): CustomDataStore(context.dataStore) {
  
       val userPref: CustomPreference<Map<String>, Int>
           get() = createCustomPreference(key, serializer)
       
       val anotherPref: CustomPreference<String, String>
           get() = createCustomPreference(anotherKey, anotherSerializer)
  }
  ```

## Testing

 And now for the best part, mocking! Using [mockito-kotlin](https://github.com/mockito/mockito-kotlin) or any other mocking framework, in your test file, do this:
 ```kotlin
 @RunWith(MockitoJUnitRunner::class)
 class Test {

   @Mock
   lateinit var appPreferences: AppPreferences
 
   fun test {
      wheneverBlocking { 
        appPreferences.booleanPreference 
      }.doReturn(MockPrimitivePreference(true))
   
      // Your values will be mocked!
      appPreferences.somePreference.get()
      appPreferences.somePreference.set()
      appPreferences.somePreference.getFlow()
      appPreferences.somePreference.getAndUpdate{ ... }
   }
 }
 ```
