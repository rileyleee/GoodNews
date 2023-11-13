plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.realm.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.saveurlife.goodnews"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.saveurlife.goodnews"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.core:core:1.12.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 네비게이션
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // 구글의 통합 위치 제공자 사용(사용자의 위치 확인 위함)
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.github.niqo01.rxplayservices:rx-play-services-location:0.4.0")

    // registerForActivityResult 관련 의존성: 호출된 액티비티 종료 시 결과값 처리를 위함
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // OSMdroid: 오프라인 지도 사용(Open Street Map)
    implementation("org.osmdroid:osmdroid-android:6.1.17")

    // REALM 의존성 추가
    implementation("io.realm.kotlin:library-base:1.12.0")
    implementation("io.realm.kotlin:library-sync:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // SharedPreferences 의존성 추가
    implementation("androidx.preference:preference-ktx:1.2.1")

    // CSV -> JSON 파일 변환 (대피소 등 시설 정보 import)
    implementation("com.opencsv:opencsv:5.8")

    // osm에서 미리 다운로드한 지도 데이터를 저장하기 위함
    implementation("com.github.MKergall:osmbonuspack:6.9.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation ("com.airbnb.android:lottie:6.1.0")

    implementation("org.modelmapper:modelmapper:3.1.1")


    // http 통신을 위한 retrofit2, okhttp3 추가


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // 응답이 json이므로 파싱 쉽게 하기위해 추가
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}
