package co.kr.whitewave.build_logic.plugin.library

import co.kr.whitewave.build_logic.extensions.libs
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class WhiteWaveLibraryConventionComposePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("co.kr.whitewave.plugin.library")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = libs.findVersion("compose-compiler").get().toString()
                }
            }
        }
    }
}
