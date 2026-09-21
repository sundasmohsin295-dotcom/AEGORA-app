# ==============================================================================
# AEGORA MILITARY-GRADE R8 PROGUARD & OBFUSCATION RULES
# ==============================================================================

# Preserve line numbers and source file attributes for clean stack traces in GlobalExceptionHandler
-keepattributes SourceFile,LineNumberTable,InnerClasses,EnclosingMethod
-keepattributes *Annotation*,Signature,Exceptions

# ------------------------------------------------------------------------------
# Kotlin Coroutines & Flow
# ------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ------------------------------------------------------------------------------
# Jetpack Room Database
# ------------------------------------------------------------------------------
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# ------------------------------------------------------------------------------
# Moshi & Retrofit Serialization
# ------------------------------------------------------------------------------
-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
-dontwarn com.squareup.moshi.**
-dontwarn retrofit2.**
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ------------------------------------------------------------------------------
# RevenueCat Purchases SDK
# ------------------------------------------------------------------------------
-keep class com.revenuecat.purchases.** { *; }
-dontwarn com.revenuecat.purchases.**

# ------------------------------------------------------------------------------
# AndroidX Security Crypto & MasterKey
# ------------------------------------------------------------------------------
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# ------------------------------------------------------------------------------
# AndroidX BiometricPrompt
# ------------------------------------------------------------------------------
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**

# ------------------------------------------------------------------------------
# Coil Image Loader
# ------------------------------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**

# ------------------------------------------------------------------------------
# Anti-Reverse Engineering & Hardening Rules
# ------------------------------------------------------------------------------
# Strip logging calls in release builds to eliminate sensitive debug telemetry leaks
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# Repackage all non-kept classes into a single flattened package to hinder decompiler navigation
-repackageclasses ''
-allowaccessmodification

# ------------------------------------------------------------------------------
# App Widgets (Jetpack Glance)
# ------------------------------------------------------------------------------
-keep class androidx.glance.** { *; }
-keep class com.example.widget.** { *; }

# ------------------------------------------------------------------------------
# Firebase SDKs
# ------------------------------------------------------------------------------
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ------------------------------------------------------------------------------
# AEGORA Core Data Models & Security Entities
# ------------------------------------------------------------------------------
-keep class com.example.model.** { *; }
-keep class com.example.security.** { *; }
-keep class com.example.util.SystemCrashReport { *; }
-keep class com.example.util.GlobalExceptionHandler { *; }
-keep class com.example.data.cloud.** { *; }
-keep class com.example.commerce.** { *; }
-keep class com.aegora.billing.** { *; }
-keep class com.example.subscription.** { *; }

# ------------------------------------------------------------------------------
# DataStore Preferences
# ------------------------------------------------------------------------------
-keep class androidx.datastore.** { *; }
-keep class com.example.data.SecurityClearanceRepository { *; }

# Prevent obfuscation of BuildConfig fields
-keepclassmembers class com.example.BuildConfig {
    public static final java.lang.String *;
    public static final boolean *;
    public static final int *;
}

