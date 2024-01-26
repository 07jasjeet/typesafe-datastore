# Typesafe-Datastore      [![](https://jitpack.io/v/07jasjeet/typesafe-datastore.svg)](https://jitpack.io/#07jasjeet/typesafe-datastore)
  Typesafe-Datastore is a **lightweight** abstraction layer on top of SharedPreferences DataStore that provides type-safety without dealing with Proto-DataStore. 
  
  Why Typesafe-Datastore?
  1. Proto-DataStore is complex, requires plugins and simply overkill for SharedPreferences. Prefer using Room if performance comes into play.
  2. If one is already using Preferences DataStore but wants type-safety, migrating to Proto-DataStore would be a lot of pain in a live app since Proto-DataStore uses Protocol Buffers under the hood. Whereas, this implementation, provides **flexibility** and **easy of migration** on a live app.
  3. Apart from type-safety, various **migration functions** have been included to migrate data inside DataStore in a type-safe way.
  4. **Testing** is really easy.

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
    implementation("com.github.07jasjeet.typesafe-datastore:typesafe-datastore:1.0.1")
    // Alternatively - Gson backed type-safe datastore
    implementation("com.github.07jasjeet.typesafe-datastore:typesafe-datastore-gson:1.0.1")   

    // Testing
    testImplementation("com.github.07jasjeet.typesafe-datastore:typesafe-datastore-test:1.0.1")
}
```
## Development

- Prerequisite: Latest version of the Android Studio and SDKs on your pc.
- Clone this repository.
- Use the `gradlew build` command to build the project directly or use the IDE to run the project to your phone or the emulator.

## Usage (AutoTypedDataStore)
`AutoTypedDataStore` has various preference creation functions that are backed by `Gson` serializer so that you don't have to write your own
serializers everytime.

To get Started, import the library by adding the following dependency.
```gradle
implementation("com.github.07jasjeet.typesafe-datastore:typesafe-datastore-gson:$version")
```
And now add preferences as follows:
```kotlin
// Your DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore("prefs")

class MyAutoTypedPreferences(context: Context): AutoTypedDataStore(context.dataStore) {
    companion object {
        val key = stringPreferencesKey("key")
    }
    
    val listPref: ComplexPreference<List<String>>
        get() = createListPreference(key)

    // or

    val mapPref: ComplexPreference<Map<String, List<String>>>
        get() = createMapPreference(key)

    // ... and many other pre-defined preferences
}
```
You can also add custom preferences without any boilerplate as follows:
```kotlin
val key = stringPreferencesKey("key")

val customPref: ComplexPreference<SomeClass>
    get() = createCustomPreference(key, defaultValue)
```

To use these preferences, simply do as follows:
```kotlin
// Acquire object by injecting or create one.
val preferences = MyAutoTypedPreferences(context)

preferences.listPref.get()
preferences.listPref.set(...)
preferences.listPref.getFlow()
preferences.listPref.getAndUpdate{ ... }
```

## Usage (TypeSafeDataStore)

If you want to use your own serialization library, you can use `TypeSafeDataStore` and create preferences. To do that, import
this library by adding the following dependency:
```gradle
implementation("com.github.07jasjeet.typesafe-datastore:typesafe-datastore:$version")
```
  ```kotlin
  class MyPreferences(context: Context): TypeSafeDataStore(context.dataStore)
      companion object {
          val key = booleanPreferencesKey("my-key")
      }

      val preference: PrimitivePreference<Boolean>
          get() = createPrimitivePreference(key, false)

      // or

      val complexPref: ComplexPreference<List<List<String>>>
          get() = createComplexPreference(key, serializer)
  ```
 
## Custom Preferences
 
  In-order to create **Custom** [DataStorePreference] you need to do some steps.
  Firstly, create a new interface as follows with your newer implementation in it.
  ```kotlin
  interface CustomPreference<T, R>: Preference<T, R> {
       // create new Functions such as getSorted() etc...
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

## Migrations

Jetpack DataStore currently has solution to migrate SharedPreference to Preferences DataStore, but there is no such shorthand solution for intra-DataStore migrations.
Migrating a simple preference from one key to another can be done as follows:
```kotlin
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "name",
    produceMigrations = {
         listOf(
             IntraDataMigration(currentKey, newKey) { currentValue ->
                  // Run transformations
                  return newValue
             }
         )
    }
)
```

## Testing

 And now for the best part, mocking! To get started, import this library by adding the following as a dependency.
 ```gradle
 testImplementation("com.github.07jasjeet.typesafe-datastore:typesafe-datastore-test:$version")
 ```
 
 Using [mockito-kotlin](https://github.com/mockito/mockito-kotlin) or any other mocking framework, in your test file, do this:
 ```kotlin
 @RunWith(MockitoJUnitRunner::class)
 class Test {

   @Mock
   lateinit var myPreferences: MyPreferences
 
   fun test {
      wheneverBlocking { 
        myPreferences.booleanPreference 
      }.doReturn(MockPrimitivePreference(true))
   
      // Your values will be mocked!
      appPreferences.booleanPreference.get()
      appPreferences.booleanPreference.set()
      appPreferences.booleanPreference.getFlow()
      appPreferences.booleanPreference.getAndUpdate{ ... }
   }
 }
 ```
