package co.kr.whitewave.build_logic.plugin.library

import co.kr.whitewave.build_logic.AndroidConfiguration.configureAndroidLibrary
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class WhiteWaveLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureAndroidLibrary(this)
            }
        }
    }
}
