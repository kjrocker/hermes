# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is "Hermes", an Android application written in Kotlin using Android's modern development stack. The app uses Navigation Component for fragment-based navigation and View Binding for type-safe view access.

**Package**: `com.kehvyn.hermes`  
**Min SDK**: 31, **Target/Compile SDK**: 36  
**Language**: Kotlin (JVM Target 11)

## Development Commands

### Build & Run
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK  
./gradlew assembleRelease

# Install debug build on connected device/emulator
./gradlew installDebug

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.kehvyn.hermes.ExampleUnitTest"
```

### Code Quality
```bash
# Lint check
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

## Architecture

### Navigation Structure
- **Single Activity**: `MainActivity` serves as the host with a navigation fragment container
- **Fragment-based UI**: Uses Navigation Component with `nav_graph.xml`
- **Current Navigation**: FirstFragment ↔ SecondFragment (bidirectional)
- **Navigation Host**: `R.id.nav_host_fragment_content_main` (note: there's inconsistency in the code with `nav_host_fragment_fragment_timer`)

### Key Components
- **MainActivity.kt**: Main entry point with toolbar, FAB, and navigation setup
- **FirstFragment.kt**: Uses View Binding pattern (`FragmentFirstBinding`)
- **SecondFragment.kt**: Standard fragment in navigation flow  
- **TimerFragment.kt**: Newly added fragment (appears to be template-generated)

### View Binding
The project uses View Binding (enabled in `build.gradle.kts`). All fragments should follow the pattern:
```kotlin
private var _binding: FragmentXxxBinding? = null
private val binding get() = _binding!!
// Set _binding = null in onDestroyView()
```

### Dependencies & Frameworks
- **AndroidX Core KTX**: Modern Android development
- **Material Design**: UI components and theming
- **Navigation Component**: Fragment navigation (`navigation-fragment-ktx`, `navigation-ui-ktx`)
- **ConstraintLayout**: Layout system
- **View Binding**: Type-safe view access

## Important Notes

### Navigation Host Fragment ID Issue
There's an inconsistency in `MainActivity.kt`:
- Line 27: References `R.id.nav_host_fragment_fragment_timer`  
- Line 55: References `R.id.nav_host_fragment_content_main`

Verify which ID is correct in the layout files before making navigation changes.

### Theme & Resources
- App theme: `@style/Theme.Hermes`
- App name: "Hermes" (defined in strings.xml)
- Standard Material Design icon set used

## File Structure Notes
- Main source: `app/src/main/java/com/kehvyn/hermes/`
- Resources: `app/src/main/res/`  
- Tests: Unit tests in `test/`, instrumented tests in `androidTest/`
- Build configuration: Version catalog in `gradle/libs.versions.toml`