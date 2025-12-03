plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.pitabmdmstudent"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.pitabmdmstudent"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "AUTH_BASE_URL", "\"https://api.penpencil.co/\"")
        buildConfigField("String", "MDM_BASE_URL", "\"https://api.penpencil.co/pi-os-backend/v1/\"")
        buildConfigField("String", "PARENT_BASE_URL", "\"https://api.penpencil.co/pi-os-backend/v1/\"")
        buildConfigField("String", "MDM_ORG_ID", "\"6593b4a9e678280018742c4c\"")
        buildConfigField("String", "PW_CLIENT_SECRET", "\"KjPXuAVfC5xbmgreETNMaL7z\"")
        buildConfigField("String", "SOCKET_URL", "\"wss://pi-os-backend.penpencil.co\"")
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
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("io.socket:socket.io-client:2.1.2")

    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // QR code generator
    implementation("com.google.zxing:core:3.5.4")

    // HypnoticCanvas - Mesh Gradient
    implementation("com.mikepenz.hypnoticcanvas:hypnoticcanvas:0.4.1")
}
