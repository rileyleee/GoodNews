// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    // 워치 앱은 compose 설정때문에 최대 버전이 1.8.10
//    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    // 그냥 앱은 1.9.0
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("io.realm.kotlin") version "1.11.0" apply false  // REALM 버전 설정
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
