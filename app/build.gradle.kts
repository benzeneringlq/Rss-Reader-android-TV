plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}
android {
    namespace = "com.start4.tvrssreader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.start4.tvrssreader"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
//    implementation("androidx.leanback:leanback:1.2.0")
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation("com.prof18.rssparser:rssparser:6.1.1")
//     https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("androidx.room:room-common:2.8.4")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")

    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:html:$markwonVersion") // 关键：解析 HTML
//    implementation("io.noties.markwon:ext-html:${markwonVersion}")
    implementation("io.noties.markwon:image-coil:$markwonVersion") // 可选：处理图片

    // 如果你还没集成 Coil 图片库本身，也需要加上
    implementation("io.coil-kt:coil:2.7.0")

    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("com.google.zxing:core:3.5.4")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.room:room-runtime:2.8.4")
    annotationProcessor("androidx.room:room-compiler:2.8.4")
// To use Kotlin annotation processing tool (kapt)
    ksp("androidx.room:room-compiler:2.8.4")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.13.2")
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-ktx
    runtimeOnly("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

}