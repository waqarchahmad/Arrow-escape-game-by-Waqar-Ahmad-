# Arrow Escape — Phone-only APK build

This folder is an add-on for the Arrow Escape Android project.

## Phone-only method
1. Create a GitHub repository, for example `ArrowEscape`.
2. Upload ALL files from the original ArrowEscape project ZIP, plus the `.github` folder from this package.
3. Commit to the `main` branch.
4. Open the repository's **Actions** tab.
5. Select **Build Arrow Escape APK**.
6. Tap **Run workflow**.
7. Wait for the green checkmark.
8. Open the completed workflow run.
9. Scroll to **Artifacts** and download `ArrowEscape-debug-apk`.
10. Extract the downloaded ZIP on your phone. Inside is `app-debug.apk`.

GitHub Actions runs the Gradle build on a cloud runner, so Android Studio/laptop is not required on your phone.

The workflow builds a debug APK for testing. A signed release APK for Play Store distribution requires signing configuration.
