plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.aistudio.cyberduel.aegora"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "GEMINI_API_KEY", "\"AQ.Ab8RN6KnwqVZGaP8qHyZTGjiTmEfcOCJRW_hZb9dL3fouUOHRg\"")
        buildConfigField("String", "REVENUECAT_PUBLIC_API_KEY", "\"goog_mock_aegora_revenuecat_key\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = true
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)
  implementation(libs.androidx.compose.material.icons.extended)

  // WorkManager for background threat hunting
  implementation(libs.androidx.work.runtime.ktx)

  // Security & Hardware Enclave
  implementation("androidx.security:security-crypto:1.1.0-alpha06")
  implementation("androidx.biometric:biometric:1.2.0-alpha05")
  implementation("org.bouncycastle:bcprov-jdk18on:1.79")
  implementation("org.bouncycastle:bcpkix-jdk18on:1.79")

  // SQLCipher Encrypted DB & Room
  implementation("net.zetetic:android-database-sqlcipher:4.5.4")
  implementation("androidx.room:room-runtime:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")

  // DataStore Preferences
  implementation("androidx.datastore:datastore-preferences:1.1.7")

  // Networking, Interceptors & Retrofit
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
  implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
  implementation("com.squareup.retrofit2:retrofit:2.12.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.12.0")
  implementation("com.squareup.moshi:moshi:1.15.2")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
  implementation("com.google.code.gson:gson:2.11.0")

  // Firebase Suite
  implementation("com.google.firebase:firebase-auth:24.2.0")
  implementation("com.google.firebase:firebase-firestore:26.6.0")
  implementation("com.google.firebase:firebase-functions:22.1.1")
  implementation("com.google.android.gms:play-services-tasks:18.4.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

  // RevenueCat In-App Purchases
  implementation("com.revenuecat.purchases:purchases:8.25.0")

  // Image Loading
  implementation("io.coil-kt:coil-compose:2.7.0")

  // Glance App Widget
  implementation("androidx.glance:glance-appwidget:1.1.1")
  implementation("androidx.glance:glance-material3:1.1.1")
}
