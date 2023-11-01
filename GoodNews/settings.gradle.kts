val properties = java.util.Properties().apply {
    java.io.FileInputStream(rootDir.resolve("gradle.properties")).use { load(it) }
}
val MAPBOX_DOWNLOADS_TOKEN: String by properties

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

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = MAPBOX_DOWNLOADS_TOKEN
            }
        }
    }
}

rootProject.name = "GoodNews"
include(":app")
 