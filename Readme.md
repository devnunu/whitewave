
# 최초 파일 구조

co.kr.whitewave/
├── WhiteWaveApplication.kt
├── data/
│   ├── model/
│   │   ├── Sound.kt
│   │   └── Preset.kt
│   ├── repository/
│   │   └── PresetRepository.kt
│   ├── local/
│   │   └── PresetDatabase.kt
│   └── player/
│       └── AudioPlayer.kt
├── di/
│   └── AppModule.kt
├── ui/
│   ├── MainActivity.kt
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   └── preset/
│   │       ├── PresetScreen.kt
│   │       └── PresetViewModel.kt
│   ├── components/
│   │   ├── SoundItem.kt
│   │   ├── VolumeSlider.kt
│   │   └── TimerPicker.kt
│   └── theme/
│       └── Theme.kt
└── service/
└── AudioService.kt