# ===== Chaquopy（Python 运行时）=====
# Python 通过原生绑定调用 Java，所有 com.chaquo.python 相关类必须保留
-keep class com.chaquo.python.** { *; }
-keep class com.chaquo.python.android.** { *; }
-dontwarn com.chaquo.python.**

# ===== kotlinx.serialization =====
# 插件已自带规则；此处兜底保留序列化注解与生成的 Serializer
-keepattributes *Annotation*, InnerClasses
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}
