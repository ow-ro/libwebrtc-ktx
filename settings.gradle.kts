dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.11.0"
////                            # available:"0.20.0"
////                            # available:"0.21.0"
}

rootProject.name = "libwebrtc-ktx"
include("core")
