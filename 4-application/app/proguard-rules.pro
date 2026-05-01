# ProGuard rules for the Quran Memoriser app.
#
# Release builds keep minifyEnabled = false (declared in app/build.gradle), so
# these rules are advisory rather than load-bearing. They are included as a
# defensive belt for any consumer who flips minification on later.

# Keep line numbers for stack-trace clarity, but redact the source filename.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Moshi reads Kotlin metadata via reflection; preserve the data classes used
# by the Quran.com API parser.
-keep class com.example.mob_dev_portfolio.data.remote.dto.** { *; }

# Hilt-generated components and modules are referenced by reflection; do not
# strip them.
-keep,allowobfuscation,allowshrinking class dagger.hilt.** { *; }

# Coroutines emits internal classes referenced via reflection by debug tools.
-dontwarn kotlinx.coroutines.debug.internal.**

# OkHttp/Retrofit are well behaved under R8 from version 4 onwards but the
# logging interceptor reflectively accesses platform classes.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
