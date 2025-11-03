package co.kr.whitewave.build_logic.plugin.application

import co.kr.whitewave.build_logic.extensions.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class WhiteWaveApplicationConventionComposePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("co.kr.whitewave.plugin.application")
            pluginManager.apply(libs.findPlugin("kotlin-compose").get().get().pluginId)

            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                    buildConfig = true
                }
            }
        }
    }
}
