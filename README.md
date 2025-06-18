Flashcard App

FlashDeck is an Android app built using Jetpack Compose that helps users create, organize, and study flashcards. It features a clean and intuitive interface for managing sets of flashcards and practicing them.
Features:

    Create and manage flashcard sets

    Add, edit, and delete individual flashcards

    Practice flashcards with a simple flip interface

    Delete flashcard sets or individual cards with a confirmation dialog


Make sure to have the following installed:

    Android Studio (latest stable version)

    Kotlin (version 1.5 or higher)

    Gradle (required for building the project)

For building and running the app on your local machine, you need an Android device or emulator set up.
Getting Started
1. Clone the repository:

git clone https://github.com/your-username/flashdeck.git
cd flashdeck

2. Open the project in Android Studio:

    Launch Android Studio and select Open an existing project.

    Browse to the cloned project folder and open it.

3. Install dependencies:

The project uses Jetpack Compose and some other dependencies. To install them, sync Gradle with the following steps:

    Click Sync Now when prompted or go to File > Sync Project with Gradle Files.

4. Build and run the app:

    Select your preferred Android Emulator or connected device.

    Press the Run button in Android Studio to build and launch the app.

App Structure

The app follows an MVVM (Model-View-ViewModel) architecture and uses Jetpack Compose for UI development.
Key components:

    CardScreen.kt - A screen where users can view, edit, or delete cards. It features the main flashcards interface.

    EditCardDialog.kt - A dialog that allows users to edit individual cards.

    FlashDeckCard.kt - Displays each flashcard set in a grid view. Allows users to navigate to the practice screen or edit the set.

    ViewModels - The CardViewModel is responsible for fetching data, handling user interactions, and managing the state.

    Composables - The UI is built using various reusable composables, including custom cards and dialogs.

Features in Progress

The following features are planned for future updates:

    User authentication (sign up/login)

    Cloud storage integration (to store flashcards in the cloud)

    Support for multiple languages

    Flashcard stats and progress tracking

Contributing

We welcome contributions from the community! To contribute:

    Fork the repository.

    Create a new branch (git checkout -b feature-branch).

    Make your changes and commit them (git commit -m 'Add new feature').

    Push to your fork (git push origin feature-branch).

    Create a pull request.

Please ensure your code follows the project’s conventions and passes tests (if applicable).
License

This project is licensed under the MIT License - see the LICENSE file for details.
Acknowledgements

    Jetpack Compose

    Kotlin Programming Language

    Android Developers community
