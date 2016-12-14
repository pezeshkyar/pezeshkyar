# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\hossein\AppData\Local\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepclassmembers class com.example.doctorsbuilding.nav.Util.ScaleImageView
#-keepclassmembers class com.example.doctorsbuilding.nav.Util.NonScrollListView
#-keepclassmembers class com.example.doctorsbuilding.nav.Util.NonScrollExpanableListView
#-dontwarn org.xmlpull.**
-dontnote android.net.http.**
-dontnote org.apache.http.**
-dontnote com.google.vending.licensing.**
-dontnote com.android.vending.licensing.**
-keepattributes Signature
-dontwarn com.viewpagerindicator.**
