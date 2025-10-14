package co.kr.whitewave.build_logic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object AndroidConfiguration {

    fun Project.configureAndroidApplication(
        extension: ApplicationExtension
    ) {
        extension.apply {
            namespace = "co.kr.whitewave"
            compileSdk = VersionInfo.compileSdkVersion

            defaultConfig {
                applicationId = "co.kr.whitewave"
                minSdk = VersionInfo.minSdkVersion
                targetSdk = VersionInfo.targetSdkVersion
                versionCode = VersionInfo.versionCode
                versionName = VersionInfo.versionName

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            configureCommonAndroidOptions(this)
        }
    }

    fun Project.configureAndroidLibrary(
        extension: LibraryExtension
    ) {
        extension.apply {
            compileSdk = VersionInfo.compileSdkVersion

            defaultConfig {
                minSdk = VersionInfo.minSdkVersion
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                consumerProguardFiles("consumer-rules.pro")
            }

            configureCommonAndroidOptions(this)
        }
    }

    private fun Project.configureCommonAndroidOptions(
        extension: CommonExtension<*, *, *, *, *, *>
    ) {
        extension.apply {
            configureBuildTypes()
            configureCompileOptions()
            configurePackaging()
        }

        configureKotlinOptions()
    }

    private fun CommonExtension<*, *, *, *, *, *>.configureBuildTypes() {
        buildTypes {
            getByName("debug") {
                isMinifyEnabled = false
            }
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }

    private fun CommonExtension<*, *, *, *, *, *>.configureCompileOptions() {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    private fun Project.configureKotlinOptions() {
        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-Xjvm-default=all"
                )
            }
        }
    }

    private fun CommonExtension<*, *, *, *, *, *>.configurePackaging() {
        packaging {
            resources {
                excludes += listOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "META-INF/DEPENDENCIES",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md"
                )
            }
        }
    }
}
