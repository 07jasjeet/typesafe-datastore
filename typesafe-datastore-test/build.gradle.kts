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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.jasjeet"
            artifactId = "typesafe-datastore-test"
            version = "1.0"
            
            pom {
                developers {
                    developer {
                        id = "Jasjeet"
                        name = "Jasjeet Singh"
                        email = "07jasjeet@gmail.com"
                    }
                }
            }
            
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}