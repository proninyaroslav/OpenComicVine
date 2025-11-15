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

-dontobfuscate

# --- Moshi sealed:type support ---
-keep class **JsonAdapter { *; }
-keepattributes RuntimeVisibleAnnotations

# Keep sealed hierarchy types (Poko + sealed:type)
-if interface * extends kotlin.sealed.SealedInterface
-keep class <1> { *; }

-if abstract class * extends kotlin.sealed.SealedClass
-keep class <1> { *; }