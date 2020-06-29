# XRciser

#### An exercise browser for Android.



## How To Install

### Build
This app can be installed on Android devices with an API level of 21 or higher, and the APK can be created in the following ways:

#### Option A: via Android Studio

This project was developed via Android Studio, and the repository should contain all the necessary project files to open this project from a fresh clone.

Once open, you can generate an APK for the project by opening the `Build` toolbar and selecting `Build Bundle(s) / APK(s) -> Build APK(s)`.

#### Option B: Gradle Build

The project can also be built on the command line via Gradle (available [here](https://docs.gradle.org/current/userguide/installation.html#installation)). To do so, navigate to the repository's root directory in the command line, and enter `gradlew assembleDebug`. 

### Installation

Either of the above options will create an `.apk` file at the path `app/build/outputs/apk/debug`, which can be transferred to and opened on an Android phone to install the application (which should be named "XRciser").
