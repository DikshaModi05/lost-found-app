plugins {
    alias(libs.plugins.android.application)
//    kotlin("android")
//    kotlin("kapt")
    id("com.google.gms.google-services") // ✅ Required for Firebase
}

android {

    namespace = "com.example.lostandfoundapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lostandfoundapp"
        minSdk = 26
        targetSdk = 35
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
    // ✅ Correct way to enable ViewBinding
    buildFeatures {
        viewBinding = true
    }
}





dependencies {

    // ✅ Use Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))

    // ✅ Firebase modules
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")

    // Other Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.auth)
    implementation(libs.picasso)
    implementation(libs.coordinatorlayout)
    implementation(libs.recyclerview)

    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Test Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.glide)
//    implementation("androidx.core:core-ktx:1.10.1")

}
