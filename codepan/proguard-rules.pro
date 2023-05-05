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

-dontwarn org.apache.**
-dontwarn com.google.common.**
-dontwarn com.mixpanel.**
-dontwarn android.support.v4.**
-dontwarn com.google.android.gms.**

-keep class net.sqlcipher.** {*;}
-keep class android.support.v8.renderscript.** {*;}
-keep class com.google.android.gms.** {*;}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class us.zoom.** {*;}
-keep class com.zipow.** {*;}
-keep class us.zipow.** {*;}
-keep class org.webrtc.** {*;}
-keep class us.google.protobuf.** {*;}
-keep class com.google.crypto.tink.** { *;}
-keep class androidx.security.crypto.**{*;}
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-printmapping mapping.txt