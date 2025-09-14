# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is "Hermes", an Android metronome/timer application written in Kotlin using Android's modern development stack. The app provides an interval timer that plays randomized audio chimes at user-specified intervals.

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

### Core Application Architecture
- **Single Activity Pattern**: `MainActivity` hosts all fragments via Navigation Component
- **Primary Feature**: `TimerFragment` - the main metronome/timer interface
- **Navigation**: Simple bidirectional flow between TimerFragment ↔ SecondFragment
- **Navigation Host**: `R.id.nav_host_fragment_content_main`

### TimerFragment - Core Functionality
The main feature is a sophisticated interval timer with:
- **Timer Controls**: Increment/decrement buttons (3-second minimum enforced)
- **Play/Pause**: Media controls for timer operation  
- **Audio System**: Random selection from multiple audio tracks per tick
- **State Management**: Proper lifecycle handling with Handler/Runnable for timing
- **Audio Resources**: Located in `app/src/main/res/raw/` - ship bell chimes with variations

### Key Technical Patterns
- **Audio Playback**: Creates MediaPlayer per playback (not persistent) for track randomization
- **Timer Implementation**: Handler + Runnable pattern for precise interval timing
- **UI Layout**: Vertical LinearLayout with nested horizontal controls for timer interface
- **Button Styling**: Transparent backgrounds with primary color text for minimal design

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
- **MediaPlayer**: Audio playback system for timer chimes
- **Handler/Looper**: Precise timing mechanism
- **View Binding**: Type-safe view access

## Audio System Architecture

### Random Track Selection
- Audio files stored in `app/src/main/res/raw/`
- Hard-coded list of track resource IDs in `TimerFragment.audioTracks`
- Each timer tick randomly selects from available tracks
- MediaPlayer instances created per playback and auto-released

### Adding New Audio Tracks
1. Place audio file in `app/src/main/res/raw/` (Android naming: lowercase, underscores only)
2. Add `R.raw.filename` to `audioTracks` list in TimerFragment
3. File will be automatically included in random selection

## Important Implementation Details

### Timer Constraints  
- **Minimum Value**: 3 seconds (prevents audio overlap issues)
- **UI Display**: Shows values with "s" suffix (e.g., "5s")
- **Controls**: Decrement disabled at minimum, no maximum limit

### Button Styling Quirks
- Play/pause button uses "||" for pause (not Unicode pause symbol)
- Unicode symbols can cause Android styling issues with transparent backgrounds
- Consistent styling: `background="@android:color/transparent"` + `textColor="?attr/colorPrimary"`

### Navigation & MainActivity
- FAB removed from MainActivity (was causing layout conflicts)
- Uses `R.id.nav_host_fragment_content_main` consistently
- Toolbar and navigation setup standard Android pattern

## File Structure Notes
- Main source: `app/src/main/java/com/kehvyn/hermes/`
- Resources: `app/src/main/res/`  
- Tests: Unit tests in `test/`, instrumented tests in `androidTest/`
- Build configuration: Version catalog in `gradle/libs.versions.toml`