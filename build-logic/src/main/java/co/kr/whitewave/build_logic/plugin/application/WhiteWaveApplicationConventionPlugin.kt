package co.kr.whitewave.build_logic.plugin.application

import co.kr.whitewave.build_logic.AndroidConfiguration.configureAndroidApplication
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class WhiteWaveApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureAndroidApplication(this)
            }
        }
    }
}
