import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "joel.highscorec.melodytileshighnight"
    compileSdk = 35

    defaultConfig {
        applicationId = "joel.highscorec.melodytileshighnight"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyección de claves desde local.properties
        resValue("string", "admob_banner_id", localProperties["ADMOB_BANNER_ID"] as String)
        resValue("string", "admob_app_id", localProperties["ADMOB_APP_ID"] as String)
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {


    implementation ("com.google.android.material:material:1.11.0") // Usa la última versión


    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    //json
    implementation(libs.gson)


    //publi
    implementation ("com.google.android.gms:play-services-ads:24.2.0")


    // Mockito para tests unitarios
    testImplementation("org.mockito:mockito-core:5.5.0")


    // Opcional si haces tests instrumentados también:
    androidTestImplementation ("org.mockito:mockito-android:5.5.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}