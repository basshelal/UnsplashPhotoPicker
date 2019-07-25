# Unsplash Photo Picker (Not yet finished!)

![Unsplash Photo Picker for Android preview](https://github.com/unsplash/unsplash-photopicker-android/blob/dev/unsplash-photo-picker-android.png "Unsplash Photo Picker for Android")

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9a219205d7cd4c1a8f6a5cf4bf4df3d8)](https://app.codacy.com/app/basshelal/UnsplashPhotoPicker?utm_source=github.com&utm_medium=referral&utm_content=basshelal/UnsplashPhotoPicker&utm_campaign=Badge_Grade_Dashboard)
[![License](https://img.shields.io/github/license/basshelal/UnsplashPhotoPicker.svg)](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/LICENSE)

[`UnsplashPhotoPicker`](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/photopicker/src/main/java/com/github/basshelal/unsplashpicker/presentation/UnsplashPhotoPicker.kt)
 is a modular and customizable Android View that allows you to quickly search the Unsplash library for free high-quality photos with just a few lines of code.

Based on [Unsplash Photo Picker for Android by Unsplash](https://github.com/unsplash/unsplash-photopicker-android).

## Table of Contents

* [Description](#description)
* [Requirements](#requirements)
* [Installation](#installation)
* [Usage](#usage)
  * [Initial Configuration](#initial-configuration)
  * [Xml Attributes](#xml-attributes)
  * [Click Listeners](#click-listeners)
* [Special Thanks](#special-thanks)
* [License](#license)

## Description

This library allows you to add a fully functioning photo picker which uses free high-quality photos photos from [Unsplash.com](https://unsplash.com/).

You can allow for single or multiple selections and enlarge any photo to view in fullscreen.

The difference between this library and the [one it is based off of](https://github.com/unsplash/unsplash-photopicker-android)
 is that this is more modular and allows for a greater level of customization, convenience and features.
 
This library provides you with a `View` you can use in your XML layouts with attributes that allow you to customize the view to your preference.
The original library only allowed you to launch an Activity with the picker in it, whereas here you can use the picker (with the search bar) 
as a `View` so it can be used as a child in `ViewGroup`s in `Fragment`s or `Activity`s.

This library also provides you with easier ways to show and select photos and click listeners to listen to click and long click events on each photo.

In a nutshell this library adds more features and customization to the original making it easier to use for the developer.

## Requirements

* Min API 21 and AndroidX
* Kotlin 1.3+
* [Unsplash API Access Key and Secret Key](https://unsplash.com/documentation#registering-your-application)

## Installation

Step 1. Add the JitPack repository to your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency in your app module `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.basshelal:UnsplashPhotoPicker:1.0.0'
}
```

## Usage

__**IMPORTANT!**__

️Before you get started, you need to register as a developer on the Unsplash [Developer](https://unsplash.com/developers) portal.
Once registered, create a new app to get an **Access Key** and a **Secret Key**.
 
 Remember you must keep **both** keys secret.

### Initial Configuration

You need to call [`UnsplashPhotoPickerConfig.init(...)`](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/photopicker/src/main/java/com/github/basshelal/unsplashpicker/UnsplashPhotoPickerConfig.kt#L39)
in your custom `Application` class's `onCreate()`, with the required arguments.

```kotlin
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        UnsplashPhotoPickerConfig.init(
            application = this,
            accessKey = "your access key",
            secretKey = "your secret key",
            isLoggingEnabled = false // optional to enable full HTTP logging
        )
    }
}


```
### Xml Attributes

All xml attributes begin with `photoPicker_`.

| name | format | default value |
|------|--------|---------------|
|photoPicker_spanCount|integer|2|
|photoPicker_hasSearch|boolean |true|
|photoPicker_persistentSearch|boolean|false|
|photoPicker_isMultipleSelection|boolean|false|
|photoPicker_errorDrawable|drawable|null|
|photoPicker_placeHolderDrawable|drawable|null|
|photoPicker_pickerPhotoSize|photoSize|small|
|photoPicker_showPhotoSize|photoSize|small|
|photoPicker_searchHint|string|"Search Unsplash Photos"|
|photoPicker_photoByString"|string|"Photo by"|
|photoPicker_onString|string|"on"|
|photoPicker_clickOpensPhoto|boolean|true|
|photoPicker_longClickSelectsPhoto|boolean|false|

PhotoSize enum:
thumb, small, medium, regular, large, full, raw
See the [`PhotoSize enum class`](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/photopicker/src/main/java/com/github/basshelal/unsplashpicker/presentation/UnsplashPhotoPicker.kt#L549)

### Click Listeners

You can listen to click events on any photo in the picker using `onClickPhoto` and `onLongClickPhoto` 
which are both anonymous functions (lambdas) which have the `UnsplashPhoto` as their first parameter
and the clicked `ImageView` as the second parameter.

```kotlin
unsplashPhotoPicker.apply {
    onClickPhoto = { unsplashPhoto, imageView -> }
    onLongClickPhoto = { unsplashPhoto, imageView -> }
}
```

## Special Thanks

Special Thanks to Unsplash for creating a beautiful free service with an easy to use API, seriously you guys are awesome.

Special Thanks again to Unsplash for the initial code that this library is based off of,
 they've very elegantly done the backend code used to perform network requests. This library would not be possible without them
 and without them making it open source and licensing it with the MIT License, open and free for all as this library is as well.
 Many thanks to them for all of this.

## License

```
MIT License

Copyright (c) 2019 Bassam Helal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```