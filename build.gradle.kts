// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    kotlin("android") version "1.9.22" apply false
//    kotlin("kapt") version "1.9.22" apply false
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}