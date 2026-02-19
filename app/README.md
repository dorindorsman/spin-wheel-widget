SpinWheelWidget

A minimal Android Home Screen Spin Wheel Widget built with Kotlin and Glance.

This library renders a spin wheel widget that fetches remote configuration and assets, caches them locally, and performs a frame-based spin animation when tapped.


## Overview

SpinWheelWidget is an Android widget library that:
•	Fetches remote JSON configuration
•	Downloads image assets from public URLs (e.g. Google Drive)
•	Caches JSON and assets locally
•	Stores last fetch time using SharedPreferences
•	Generates animation frames
•	Renders a Home Screen Widget using Glance
•	Spins the wheel on tap (frame-step animation)


## Architecture

spinwheel/
│
├── config/ SpinWheelConstants.kt
├── data/
│   ├── SpinWheelApi.kt
│   ├── SpinWheelRepository.kt
│   ├── SpinWheelCacheManager.kt
│
├── model/ConfigModels.kt
├── prefs/
│   └── SpinWheelPrefs.kt
│
├── widget/
│   ├── SpinWheelWidget.kt
│   ├── SpinWheelWidgetReceiver.kt
│   ├── action/ SpinWheelActionCallback.kt
│   └── ui/
            ├──SpinWheelFramesGenerator.kt
            ├──SpinWheelRenderer.kt
            ├──SpinWheelWidget.kt
│
├── worker/
    ├──SpinWheelWarmup.kt
    ├──SpinWheelWarmupWorker.kt
└── SpinWheelSdk.kt

## Remote Configuration

The widget expects a remote JSON configuration in the following minimal format:

{
"network": {
"assets": {
"host": "https://public-host/"
}
},
"wheel": {
"assets": {
"bg": "bg.jpeg",
"wheel": "wheel.png",
"frame": "wheel-frame.png",
"spin": "wheel-spin.png"
}
}
}

Assets are resolved as:
host + relative_path

## Usage
1. Warmup / Refresh

Call from your Application or Activity:
SpinWheelSdk.refreshIfNeeded(
context,
SpinWheelConstants.CONFIG_URL
)
This will:
•	Fetch remote JSON
•	Download assets if missing
•	Generate animation frames
•	Store last fetch timestamp


2. Add Widget to Home Screen
    1.	Long press home screen
    2.	Select Widgets
    3.	Choose SpinWheelWidget
    4.	Add to screen

## Widget UI

Layout structure:
•	Background image fills entire widget
•	Wheel centered
•	Frame overlay centered
•	Spin button clickable
•	On tap → frame-step animation

## Caching & Persistence
•	JSON cached locally
•	Assets stored in internal app storage
•	Frame images generated once
•	Last fetch time stored in SharedPreferences
•	Prevents unnecessary network calls

## Readiness Check

To verify that the widget is ready:
val cache = SpinWheelCacheManager(context)

val ready =
cache.getAssetFile("bg.jpeg").exists() &&
cache.getAssetFile("wheel.png").exists() &&
cache.getAssetFile("wheel-frame.png").exists() &&
cache.getAssetFile("wheel-spin.png").exists() &&
cache.framesCount() >= 12

## Build

To generate release AAR:
./gradlew :spinwheel:assembleRelease
Output:
spinwheel-release.aar

## Author
Dorin Dorsman
Android Developer