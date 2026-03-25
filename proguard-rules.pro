# ProGuard rules for kite-aos

# --- Project Specific Rules (Fixing "Missing class" errors) ---
# Prevent R8 from removing navigation, styling, and splash screen classes
-keep class presentation.core.navigation.impl.source.host.** { *; }
-keep class presentation.core.styling.source.theme.** { *; }
-keep class presentation.core.ui.core.splash.** { *; }

# Ignore warnings for these packages to avoid build failure if some references are missing
-dontwarn presentation.core.navigation.impl.**
-dontwarn presentation.core.styling.**

# --- Ktor / JVM Management fix (Fixing ManagementFactory error) ---
# Android doesn't have java.lang.management, so we ignore these warnings from Ktor
-dontwarn java.lang.management.**
-dontwarn io.ktor.util.debug.IntellijIdeaDebugDetector

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keep class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keep class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    public <init>();
}

# --- kotlinx-serialization ---
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keepclassmembernames class kotlinx.serialization.json.** {
    *** serializer(...);
}
-keepclassmembernames class * {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.Serializable *** Companion;
    @kotlinx.serialization.Serializable *** $serializer;
}

# --- Jetpack Compose ---
-keep class androidx.compose.ui.platform.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembernames class * {
    @androidx.compose.runtime.Composable *;
}

# --- Arrow ---
-keep class arrow.core.** { *; }
-keep class arrow.fx.coroutines.** { *; }
-keepclassmembers class * extends arrow.core.Option { *; }
-keepclassmembers class * extends arrow.core.Either { *; }

# --- Koin (Dependency Injection) ---
-keep class org.koin.** { *; }
-keepclassmembernames class * {
    @org.koin.core.annotation.* *;
}

# --- Room (Persistence) ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class * {
    @androidx.room.Dao *;
    @androidx.room.Database *;
    @androidx.room.Entity *;
}

# --- KMQTT ---
-keep class io.github.davidepianca98.kmqtt.** { *; }

# --- R8 Optimization & Debugging ---
-dontusemixedcaseclassnames
-dontpreverify
-verbose
-keepattributes SourceFile, LineNumberTable

# --- Standard Android rules ---
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# --- Protobuf Specific Rules ---

# Keep all generated Proto messages and their fields
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# Specifically for your MqttDataProto
-keep class data.preferences.impl.proto.MqttDataProto$** { *; }

# Allow Protobuf to use reflection for message schema
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**