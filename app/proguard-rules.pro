# Add project specific ProGuard rules here.
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class com.yatharth.whatsappscheduler.data.local.entity.** { *; }
