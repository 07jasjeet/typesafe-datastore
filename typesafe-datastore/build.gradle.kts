
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish") version "0.27.0"
}

android {
    namespace = "com.jasjeet.typesafe_datastore"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    testImplementation("junit:junit:4.13.2")
}

@Suppress("UnstableApiUsage")
mavenPublishing {
    configure(AndroidSingleVariantLibrary(
        // the published variant
        variant = "release",
        // whether to publish a sources jar
        sourcesJar = true,
        // whether to publish a javadoc jar
        publishJavadocJar = true,
    ))
    
    publishToMavenCentral(SonatypeHost.S01)
    
    signAllPublications()
    coordinates("com.github.07jasjeet", "typesafe-datastore", "1.0")
    
    pom {
        name.set(project.name)
        description.set("TypeSafeDataStore is a lightweight abstraction layer on top of SharedPreferences " +
                "DataStore that provides type-safety without dealing with Proto-DataStore.")
        inceptionYear.set("2024")
        url.set("https://github.com/07jasjeet/TypeSafe-DataStore/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id = "Jasjeet"
                name = "Jasjeet Singh"
                email = "07jasjeet@gmail.com"
            }
        }
    }
}