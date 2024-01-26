pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "typesafe-datastore"
include(":app")
include(":typesafe-datastore")
include(":typesafe-datastore-test")
include(":typesafe-datastore-gson")
