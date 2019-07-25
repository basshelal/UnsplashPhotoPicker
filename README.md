# Unsplash Photo Picker (Not yet finished!)

![Unsplash Photo Picker](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/pictures/Unsplash-Photo-Picker.png)

[![JitPack](https://jitpack.io/v/basshelal/UnsplashPhotoPicker.svg)](https://jitpack.io/#basshelal/UnsplashPhotoPicker)
![minAPI 21](https://img.shields.io/badge/minAPI-21-green.svg)
![Kotlin 1.3+](https://img.shields.io/badge/Kotlin-1.3%2B-orange.svg)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e5086f93fee54ad88887859e0b07af49)](https://www.codacy.com/app/basshelal/UnsplashPhotoPicker?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=basshelal/UnsplashPhotoPicker&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/github/license/basshelal/UnsplashPhotoPicker.svg)](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/LICENSE)

Unsplash Photo Picker
 is a modular and customizable Android View that allows you to quickly search [Unsplash.com](https://unsplash.com/) for free high-quality photos with just a few lines of code.

Based on [Unsplash Photo Picker for Android by Unsplash](https://github.com/unsplash/unsplash-photopicker-android), this library adds more flexibility, conveniences and customization than the original. 

The original only launched an `Activity` for you which you had no control of, this provides you with a `View` you can use in your layouts.

## Contents

* [Description](#description)
* [Requirements](#requirements)
* [Installation](#installation)
* [Usage](#usage)
  * [Initial Configuration](#initial-configuration)
  * [Xml Attributes](#xml-attributes)
  * [Click Listeners](#click-listeners)
  * [Abiding by the Unsplash API Guidelines](#abiding-by-the-unsplash-api-guidelines)
* [Special Thanks](#special-thanks)
* [License](#license)

## Description

This library allows you to add a fully functioning photo picker which uses free high-quality photos from [Unsplash.com](https://unsplash.com/).

You can select multiple (or one) image and you can show any image in fullscreen.

This library will also take care of following ***most*** of the [Unsplash **Technical** API Guidelines](https://help.unsplash.com/en/articles/2511245-unsplash-api-guidelines) for you, however I am not responsible or liable if you do not follow them yourself.

See the [Abiding by the Unsplash API Guidelines](#abiding-by-the-unsplash-api-guidelines) section for more details.

## Requirements

* Min API 21 and AndroidX
* Kotlin 1.3+
* [Unsplash API Access Key and Secret Key](https://unsplash.com/documentation#registering-your-application)

## Installation

Add the JitPack repository to your **root** `build.gradle` at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency in your **app module** `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.basshelal:UnsplashPhotoPicker:1.0.0'
}
```

## Usage

⚠️ **IMPORTANT!** ⚠️

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
            unsplashAppName = "your app's name on Unsplash",
            isLoggingEnabled = false // optional to enable full HTTP logging, defaults to false
        )
    }
}
```

### Xml Attributes

All xml attributes begin with `photoPicker_`.#

For the full documentation, check out [Attributes.md](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/Attributes.md) 
or the
[`UnsplashPhotoPicker`](https://github.com/basshelal/UnsplashPhotoPicker/blob/master/photopicker/src/main/java/com/github/basshelal/unsplashpicker/presentation/UnsplashPhotoPicker.kt) class itself.

All public APIs in this library are well documented.

| name | format | default value |
|------|--------|---------------|
|photoPicker_pageSize|integer|50|
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
    onClickPhoto = { unsplashPhoto, imageView -> this.showPhoto(unsplashPhoto) }
    onLongClickPhoto = { unsplashPhoto, imageView -> this.selectPhoto(unsplashPhoto) }
}
```

### Abiding by the Unsplash API Guidelines

⚠️ **IMPORTANT!** ⚠️

You must follow the [Unsplash API Guidelines](https://help.unsplash.com/en/articles/2511245-unsplash-api-guidelines) 
when using the Unsplash API in your application.

This application takes care of **Technical** Guidelines 1-3 for you however, I am not responsible or liable if you do not follow them yourself.

Below are the Technical guidelines and how this library follows and implements them.

1. *All API uses must use the hotlinked image URLs returned by the API under the `photo.urls`  properties. This applies to all uses of the image and not just search results.*

This is automatically done for you courtesy of the original library, do not worry about this.

2. *When your application performs something similar to a download (like when a user chooses the image to include in a blog post, set as a header, etc.), you must send a request to the download endpoint returned under the `photo.links.download_location` property.*

When you are done with your images you must call `UnsplashPhotoPicker.downloadPhotos` to send the download request.

3. *When displaying a photo from Unsplash, your application must attribute Unsplash, the Unsplash photographer, and contain a link back to their Unsplash profile. All links back to Unsplash should use utm parameters in the `?utm_source=your_app_name&utm_medium=referral`.*

This is done for you as you provide the Unsplash app name when you call `UnsplashPickerConfig.init(...)`. This must be the name of your app on the Unsplash developer portal.

4. *Your application’s Access Key and Secret Key  must remain confidential. This means that they cannot be included in the client or made public. In most cases, this will require proxying the API through your own endpoint to sign the request with your keys.*

This one's on you. I recommend you make a `Keys.kt` file containing 2 `String` `const val`s, one for the Access key and one for the Secret key and ***DON'T*** check that file into Version Control by adding it to the `.gitignore` file. This solution has worked for me but be sure to test it yourself. Again, I am not responsible or liable if you mess up.

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