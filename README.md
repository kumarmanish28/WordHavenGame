# Word Haven - Production Word Puzzle Game

Word Haven is a high-quality Android word puzzle game inspired by Wordscapes. It features a custom-built circular letter wheel, a crossword-style grid with glassmorphism effects, and a robust persistence system.

## Architectural Choices

The project is built using **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** pattern to ensure scalability, testability, and a clear separation of concerns:

- **Clean Architecture Layers**:
    - **Data Layer**: Implements repositories and data sources. It handles persistent storage using **Jetpack DataStore** and loads static level content from JSON assets using **Kotlinx Serialization**.
    - **Domain Layer**: The heart of the app, containing business logic (`GameplayEngine`), validation logic (`WordValidator`), and data models. It is completely independent of Android frameworks.
    - **Presentation Layer**: A fully reactive UI built with **Jetpack Compose** and **Material 3**. ViewModels manage UI state using `StateFlow` and handle navigation logic.
- **Dependency Injection**: Powered by **Hilt**, ensuring clean dependency management and easy testing across all layers.
- **Persistence**: User progress (current level, unlocked levels, and completed levels) is persisted using **Preferences DataStore**, which provides a thread-safe and robust alternative to SharedPreferences.

## Custom Swipe Logic

The **Letter Wheel** is a sophisticated custom component implemented using the **Compose Canvas API**:

- **Gesture Handling**: Uses the `pointerInput` and `detectDragGestures` APIs to capture smooth, continuous touch events.
- **Node Resolution**: Implements a distance-based collision detection algorithm to determine which letter the user is currently touching or swiping over.
- **Dynamic Visuals**: 
    - **Connection Lines**: Real-time rendering of lines between selected letters with rounded caps and glowing primary colors.
    - **Haptic Feedback**: Visual nodes transform with an outer glow and color shift upon selection.
    - **Word Preview**: A dynamic capsule bubble appears at the top of the wheel during a swipe to show the currently formed word in real-time.

## Level Structure

The game's content is structured as a declarative JSON dataset found in `assets/levels.json`. This approach allows for easy level expansion without modifying the source code.

### Schema Example:
```json
{
  "id": 1,
  "letters": ["L", "I", "V", "E"],
  "gridWords": [
    {
      "word": "LIVE", 
      "row": 0, 
      "col": 0, 
      "isVertical": false
    },
    {
      "word": "VILE", 
      "row": 0, 
      "col": 0, 
      "isVertical": true
    }
  ],
  "bonusWords": ["VEIL"]
}
```
- **Letters**: The set of characters available on the swiping wheel.
- **GridWords**: Defines the specific placement, orientation (Vertical/Horizontal), and starting coordinates for words within the crossword grid.
- **BonusWords**: Valid words that can be formed but are not required to complete the grid (allowing for future reward expansion).

## Key Production Features
- **Staggered Animations**: Letters reveal one-by-one with a "dancing" oscillation once a word is found.
- **Visual Persistence**: The app automatically saves the current level state, allowing users to resume exactly where they left off.
- **Game Completion**: A global reset and congratulations system for when the user conquers all available levels.
- **Sound & Feedback**: Integrated audio cues for success and failure, paired with a screen-shake engine for incorrect submissions.
