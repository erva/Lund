-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-optimizationpasses 5
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-repackageclasses ''

-printmapping mapping.txt

#Kotlin
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.** { *; }
-keep class org.jetbrains.** { *; }

#CellAdapter
-dontwarn io.erva.celladapter.**
-keep public class kotlin.reflect.jvm.internal.impl.builtins.* { public *; }
-keepclassmembers class * extends io.erva.celladapter.** {
    <init>(android.view.View);
}