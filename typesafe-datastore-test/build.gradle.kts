plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.jasjeet.typesafe_datastore_test"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = BuildVersions.Java.javaVersion
        targetCompatibility = BuildVersions.Java.javaVersion
    }
    kotlinOptions {
        jvmTarget = BuildVersions.Java.javaVersionNumber
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(project(":typesafe-datastore"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.07jasjeet"
            artifactId = "typesafe-datastore-test"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}