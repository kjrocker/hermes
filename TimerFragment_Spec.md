# TimerFragment Specification

## Overview
Timer fragment with a large number display and increment/decrement controls using a simple horizontal LinearLayout.

## Layout Architecture Decision
**Changed from NestedScrollView + ConstraintLayout to horizontal LinearLayout**

**Rationale:**
- **Better fit**: NestedScrollView is for scrollable content; this is a static layout
- **Simpler**: LinearLayout is perfect for horizontal arrangement of 3 elements
- **Performance**: Flatter view hierarchy, better performance
- **Maintainability**: Easier to understand and modify than ConstraintLayout for this use case

**Files:**
- `app/src/main/java/com/kehvyn/hermes/TimerFragment.kt`
- `app/src/main/res/layout/fragment_timer.xml`

## Current Implementation Status

### TimerFragment.kt
- [x] Fragment class extends Fragment
- [x] View binding setup (`FragmentTimerBinding`)
- [x] Proper binding lifecycle management
- [ ] Timer value state management
- [ ] Increment/decrement button handlers
- [ ] Timer display logic

### fragment_timer.xml Layout
- [x] LinearLayout root container (horizontal orientation)
- [x] Center gravity and 16dp padding
- [ ] Large number display (TextView)
- [ ] Decrement button (left side)
- [ ] Increment button (right side)
- [ ] Proper LinearLayout positioning

## Required UI Specifications

### Layout Structure
- [ ] Central large number display
  - [ ] Large text size (48sp or greater)
  - [ ] Center alignment
  - [ ] Monospace font family for consistent width
  - [ ] Initial value: 0

### Control Buttons
- [ ] Decrement button (-)
  - [ ] Positioned to the left of number display
  - [ ] Material Design styled button
  - [ ] Accessible content description
  - [ ] Touch target size ≥ 48dp

- [ ] Increment button (+)
  - [ ] Positioned to the right of number display
  - [ ] Material Design styled button
  - [ ] Accessible content description
  - [ ] Touch target size ≥ 48dp

### Functionality Requirements
- [ ] Timer value state variable
- [ ] Increment button increases value by 1
- [ ] Decrement button decreases value by 1
- [ ] Prevent negative values
- [ ] Update display when value changes
- [ ] Persist value during configuration changes

### Accessibility
- [ ] Content descriptions for all interactive elements
- [ ] Proper focus handling
- [ ] Screen reader compatibility
- [ ] High contrast support

### Visual Polish
- [ ] Consistent spacing between elements
- [ ] Material Design theming
- [ ] Responsive layout for different screen sizes
- [ ] Visual feedback on button press

## Implementation Checklist

### Phase 1: Basic UI
- [x] Updated to use horizontal LinearLayout root
- [ ] Add large TextView for number display
- [ ] Add decrement button with "-" text
- [ ] Add increment button with "+" text
- [ ] Set up LinearLayout positioning with proper weights/margins

### Phase 2: Functionality
- [ ] Add timer value variable to TimerFragment
- [ ] Implement increment button click handler
- [ ] Implement decrement button click handler
- [ ] Update display TextView when value changes

### Phase 3: Polish
- [ ] Add proper styling and theming
- [ ] Implement accessibility features
- [ ] Add input validation
- [ ] Test on different screen sizes