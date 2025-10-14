plugins {
    alias(libs.plugins.whitewave.library.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "co.kr.whitewave.data"
}

dependencies {
    // AndroidX
    implementation(libs.androidx.datastore.preferences)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.androidx.media)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // billing
    implementation(libs.android.billingclient)

}