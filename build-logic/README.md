# Build Logic Module

WhiteWave 프로젝트의 Gradle Convention Plugin 모듈입니다. 이 모듈은 앱과 라이브러리 모듈의 공통 빌드 설정을 중앙화하여 관리합니다.

## 구조

```
build-logic/
├── build.gradle.kts                 # build-logic 모듈 빌드 설정
├── settings.gradle.kts              # build-logic 모듈 설정
└── src/main/java/co/kr/whitewave/build_logic/
    ├── VersionInfo.kt               # SDK 버전 및 앱 버전 정보
    ├── AndroidConfiguration.kt      # 공통 Android 설정
    ├── extensions/
    │   └── ProjectExtensions.kt     # Gradle 확장 함수
    └── plugin/
        ├── application/
        │   ├── WhiteWaveApplicationConventionPlugin.kt
        │   └── WhiteWaveApplicationConventionComposePlugin.kt
        └── library/
            ├── WhiteWaveLibraryConventionPlugin.kt
            └── WhiteWaveLibraryConventionComposePlugin.kt
```

## 제공되는 플러그인

### 1. Application Convention Plugin
- **ID**: `co.kr.whitewave.plugin.application`
- **적용 플러그인**:
  - `com.android.application`
  - `org.jetbrains.kotlin.android`
- **설정 내용**:
  - SDK 버전 (compileSdk, minSdk, targetSdk)
  - Application ID 및 버전 정보
  - Java 17 호환성
  - Kotlin 컴파일 옵션
  - Proguard 설정

### 2. Application Compose Convention Plugin
- **ID**: `co.kr.whitewave.plugin.application.compose`
- **의존**: `co.kr.whitewave.plugin.application`
- **추가 설정**:
  - Compose 빌드 기능 활성화
  - BuildConfig 기능 활성화
  - Compose Compiler Extension 버전 설정

### 3. Library Convention Plugin
- **ID**: `co.kr.whitewave.plugin.library`
- **적용 플러그인**:
  - `com.android.library`
  - `org.jetbrains.kotlin.android`
- **설정 내용**:
  - SDK 버전 설정
  - Java 17 호환성
  - Kotlin 컴파일 옵션
  - Consumer Proguard 설정

### 4. Library Compose Convention Plugin
- **ID**: `co.kr.whitewave.plugin.library.compose`
- **의존**: `co.kr.whitewave.plugin.library`
- **추가 설정**:
  - Compose 빌드 기능 활성화
  - Compose Compiler Extension 버전 설정

## 사용 방법

### 1. 기본 Application 모듈

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.whitewave.application.compose)
    alias(libs.plugins.ksp)
}

dependencies {
    // 의존성 추가
}
```

### 2. Compose를 사용하지 않는 Library 모듈

```kotlin
// feature/build.gradle.kts
plugins {
    alias(libs.plugins.whitewave.library)
}

dependencies {
    // 의존성 추가
}
```

### 3. Compose를 사용하는 Library 모듈

```kotlin
// ui/build.gradle.kts
plugins {
    alias(libs.plugins.whitewave.library.compose)
}

dependencies {
    // 의존성 추가
}
```

## 버전 관리

### SDK 버전 및 앱 버전 수정

`build-logic/src/main/java/co/kr/whitewave/build_logic/VersionInfo.kt` 파일에서 관리:

```kotlin
object VersionInfo {
    const val compileSdkVersion = 35
    const val targetSdkVersion = 35
    const val minSdkVersion = 26
    const val versionCode = 1
    const val versionName = "1.0.0"
}
```

### Compose Compiler 버전 수정

`gradle/libs.versions.toml` 파일에서 관리:

```toml
[versions]
compose-compiler = "1.5.14"
```

## 공통 설정 내용

### Kotlin 컴파일 옵션
- JVM Target: 17
- 컴파일러 플래그:
  - `-opt-in=kotlin.RequiresOptIn`
  - `-Xjvm-default=all`

### Packaging 옵션
다음 리소스는 자동으로 제외됩니다:
- `/META-INF/{AL2.0,LGPL2.1}`
- `META-INF/DEPENDENCIES`
- `META-INF/NOTICE*`
- `META-INF/LICENSE*`

### Build Types
- **debug**: minify 비활성화
- **release**: minify 비활성화, proguard 설정 적용

## 새 모듈 추가 시

1. `settings.gradle.kts`에 모듈 추가:
```kotlin
include(":feature:new-feature")
```

2. 모듈의 `build.gradle.kts`에 적절한 플러그인 적용:
```kotlin
plugins {
    alias(libs.plugins.whitewave.library.compose)
}
```

3. 필요한 의존성 추가

## 이점

1. **중복 제거**: 모든 모듈에서 반복되는 설정을 한 곳에서 관리
2. **일관성**: 모든 모듈이 동일한 설정 사용
3. **유지보수성**: 버전 업데이트 시 한 곳만 수정
4. **가독성**: 모듈의 build.gradle.kts가 간결해짐
5. **타입 안정성**: Kotlin DSL의 타입 안정성 활용

## 주의사항

- `build-logic` 모듈의 의존성은 Version Catalog를 직접 사용하지 않음
- Gradle 또는 Kotlin 버전 변경 시 `build-logic/build.gradle.kts`의 의존성도 함께 업데이트 필요
- 플러그인 변경 후에는 Gradle Sync 필수
