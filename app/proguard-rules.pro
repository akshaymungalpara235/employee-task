# Retrofit / OkHttp / Gson keep rules for release builds with code shrinking enabled.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.employeedirectory.app.data.employeedirectory.remote.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
