# XRciser

#### An exercise browser for Android.

This project was created between the 22nd and 29th of June 2020, as part of an application to the role of Android Developer at Gymondo.

I believe that I was able to fully implement all of the user stories described in the exercise, with one or two known issues described below.

As my first experience with Kotlin and one of my first with building Android applications in this way, I thoroughly enjoyed it and learned a great deal.

## Table of Contents

1. [How To Install](#how-to-install)
    * [Build](#build)
    * [Installation](#installation)
2. [Decisions & Assumptions](#decisions--assumptions)
    * [Implementation Language](#implementation-language)
    * [Search Implementation](#search-implementation)
    * [Assumptions](#assumptions)
3. [Known Issues](#known-issues)
    * [Search Bar Hint](#search-bar-hint-not-customised)
    * [One Page of Images](#only-one--page--of-images-loaded-per-exercise)
    * [Infrequent Crashes](#infrequent-crashes)

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

## Decisions & Assumptions

### Implementation Language

For this project, I chose Kotlin as the implementation language, primarily because I had no previous experience with it and thought it would make for an interesting learning experience.

This decision was also borne out by the Kotlin-first nature of much of the available Android documentation and community support.

### Search Implementation

User Story #3 describes the intended search criteria as "the name of the exercise", but does not make clear whether this should include partial matches or not. However, given the further requirement that results should be paginated, and the fact that I couldn't find any collection of 20+ exercises with identical names, I assumed that partial search was required.

This presented a challenge as the API provided, as far as I could see, provided no option for partial search - it could filter results by an exact, case-sensitive match, but partial results returned nothing. With this in mind, I designed `ExerciseClient`, the object responsible for providing exercises to the rest of the codebase, to query the server for exercises by any name and perform the search term-based filtering in the frontend, while maintaining pagination.

The implementation of this design is maybe a little convoluted, as it involves a sort of "recursive subscription" strategy to continually load new pages from the server and updating the UI with matching results until 20 total matching results (i.e. a full "client page") are found, at which point the "loading spinner" is disabled and the URL of the next "server page" is stored for the next time that the user reaches the bottom of the list and needs to load a new client page.

Despite this functionality feeling somehow overengineered, I believe it's quite robust.

### Assumptions

The following is a list of assumptions I made about the intended design of the application that was not fully clear from the provided specifications:

* The "body part" described in User Story #3 equates to a "category" in API terminology. To this end, body parts are referred to as "categories" throughout the codebase, and only as "body parts" in strings.
* Only approved exercises should be returned by the server. With no way for the user to submit their own exercises, having user-submitted exercises in the list made less sense, and severely impacted the usability of the app by blowing up the list with duplicates and unreadable entries.
* Only English-language exercises should be returned. A future step would be to include a language filter in the application, but without that, showing multiple languages at once made the app a little less readable, and as the assignment was written in English, I chose that language as the default.
* Both "name" and "original name" should be checked when searching for an exercise. This is to account for the hypothetical situation where the name of an exercise has been changed since the user last opened the app, and would allow them to still find the exercise with the name that they know.
* Search should be case-insensitive and include partial matches, i.e. searching for "bell bench" should still return "Barbell Bench Press". See the "Search Implementation" section above for more details.

## Known Issues

### Search bar hint not customised

You may notice that the project holds a `res/xml/searchable.xml` file, intended to configure the search bar in the main activity, including a custom hint to more clearly direct the user as to how to search for exercises.

Unfortunately, I seem to have missed something in connecting this up, and I didn't manage to get back around to fixing this issue before the submission deadline.

### Only one "page" of images loaded per exercise

When opening the info view for a particular exercise, the application currently only loads one "page" of images from the server to display, up to a maximum of 20. 

If there are more images, they will not be loaded as there is currently no functionality to load the next page. Future work would involve altering `ImageClient.getImagesForExercise` to return an observable of `PagedResult<ExerciseImage>` that would continue to emit subsequent page results until it reached the end.

### Infrequent crashes

It's possible for the app to crash due to an uncaught `SocketTimeoutException`, due to some request exceeding the 20 second timeout limit (implemented as suggested by [this StackOverflow page](https://stackoverflow.com/questions/53369481/app-crashes-on-java-net-sockettimeoutexception-timeout-kotlin-retrofit)). 

One solution to this problem would have been to brute-force the problem by simply increasing the timeout limit to a much higher value. 

However, this would have impacted the functionality of the app in other places and would not have solved the core issue, so I chose instead to try and fix the issue through a number of methods that you can see in the commit history. Sadly, while the frequency of crashes was sharply reduced, the problem is not totally gone, and users may experience sudden crashes while browsing.
