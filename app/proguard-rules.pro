-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-printmapping mapping.txt

#Kotlin
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.** { *; }
-keep class org.jetbrains.** { *; }

#CellAdapter
-keepclasseswithmembers public class * extends io.erva.celladapter.** { *; }
-keepclassmembers class * extends io.erva.celladapter.Cell {
    <init>(android.view.View);
}