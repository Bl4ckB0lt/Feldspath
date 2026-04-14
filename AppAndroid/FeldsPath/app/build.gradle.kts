plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.feldspath"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.feldspath"
        minSdk = 25
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.paho.mqtt)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val room_version = "2.6.1"
// bibliothèques permettant d'intéragir avec une BD SQLite
    implementation("androidx.room:room-runtime:$room_version")
// permet de prendre en compte les annotations Room
    annotationProcessor("androidx.room:room-compiler:$room_version")
// permettant d'utiliser des types réactifs comme Observable
// utiles lors des accès asynchrones
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")

}