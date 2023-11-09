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
        maven{// 지도 저장 위한 osmbonuspack 라이브러리 저장소
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "GoodNews"
include(":app")
 