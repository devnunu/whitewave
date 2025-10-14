plugins {
    `kotlin-dsl`
}

group = "co.kr.whitewave.build_logic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // build-logic의 의존성에서는 Version Catalog를 직접 사용하는 것이 권장되지 않음
    // Gradle 및 kotlin 버전 변경에 따라 수동으로 변경 필요
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("whiteWaveApplicationConvention") {
            id = "co.kr.whitewave.plugin.application"
            implementationClass = "co.kr.whitewave.build_logic.plugin.application.WhiteWaveApplicationConventionPlugin"
        }
        register("whiteWaveApplicationConventionCompose") {
            id = "co.kr.whitewave.plugin.application.compose"
            implementationClass = "co.kr.whitewave.build_logic.plugin.application.WhiteWaveApplicationConventionComposePlugin"
        }
        register("whiteWaveLibraryConvention") {
            id = "co.kr.whitewave.plugin.library"
            implementationClass = "co.kr.whitewave.build_logic.plugin.library.WhiteWaveLibraryConventionPlugin"
        }
        register("whiteWaveLibraryConventionCompose") {
            id = "co.kr.whitewave.plugin.library.compose"
            implementationClass = "co.kr.whitewave.build_logic.plugin.library.WhiteWaveLibraryConventionComposePlugin"
        }
    }
}
