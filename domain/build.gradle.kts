plugins {
    alias(libs.plugins.whitewave.library.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "co.kr.whitewave.domain"
}

dependencies {
    // Koin
    implementation(libs.koin.android)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // billing
    implementation(libs.android.billingclient)

}