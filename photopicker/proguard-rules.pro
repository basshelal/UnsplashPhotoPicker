# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-dontwarn com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
-dontwarn io.reactivex.functions.Function
-dontwarn rx.internal.util.**
-dontwarn sun.misc.Unsafe

-keep class com.github.basshelal.unsplashpicker.data.UnsplashLinks {*;}
-keepclassmembers class com.github.basshelal.unsplashpicker.data.UnsplashLinks {
 <fields>;
 <init>();
 <methods>;
}

-keep class com.github.basshelal.unsplashpicker.data.UnsplashPhoto {*;}
-keepclassmembers class com.github.basshelal.unsplashpicker.data.UnsplashPhoto {
 <fields>;
 <init>();
 <methods>;
}

-keep class com.github.basshelal.unsplashpicker.data.NetworkEndpoints {*;}
-keepclassmembers class com.github.basshelal.unsplashpicker.data.NetworkEndpoints {
 <fields>;
 <init>();
 <methods>;
}

-keep class com.github.basshelal.unsplashpicker.data.SearchResponse {*;}
-keepclassmembers class com.github.basshelal.unsplashpicker.data.SearchResponse {
 <fields>;
 <init>();
 <methods>;
}

-keep class com.github.basshelal.unsplashpicker.data.UnsplashUrls {*;}
-keepclassmembers class com.github.basshelal.unsplashpicker.data.UnsplashUrls {
 <fields>;
 <init>();
 <methods>;
}

-keep class com.github.basshelal.unsplashpicker.data.UnsplashUser {*;}
-keepclassmembers class com.github.basshelal.unsplashpicker.data.UnsplashUser {
 <fields>;
 <init>();
 <methods>;
}
