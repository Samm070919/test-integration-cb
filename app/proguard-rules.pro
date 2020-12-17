# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Daniel Ruiz\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-basedirectory com.pagatodoholdings.posandroid
-repackageclasses
-allowaccessmodification

-ignorewarnings
-keepattributes *Annotation*,EnclosingMethod

-keep class javax.xml.bind.annotation.** {*;}
-keep class com.itextpdf.text.** {*;}
-keep class com.google.** { *; }
-keep class com.crashlytics.android.core.** {*;}
-keep class com.google.firebase.crash.** {*;}

-keep class com.common.sdk.idcard.** {*;}
-keep class com.zkteco.android.** {*;}

-keep class com.google.android.gms.** { *; }
-keepnames class com.google.android.gms.** { *; }
-keepclassmembers class com.google.android.gms.** { *; }

-keep class com.google.gson.** { *; }
-keepnames class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer


-keepattributes InnerClasses


-keep class com.telpo.tps550.api.** { *; }
-keepnames class com.telpo.tps550.api.** { *; }
-keepclassmembers class com.telpo.tps550.api.** { *; }

-keep class com.common.sdk.** { *; }
-keepnames class com.common.sdk..** { *; }
-keepclassmembers class com.common.sdk..** { *; }


-keep class com.telpo.tps550.api.hdmi.HdmiCtrl { <init>(); }
-keepclassmembers class com.telpo.tps550.api.hdmi.HdmiCtrl  { <fields>; }
-keep class android.util.Vendor { <init>(); }
-keepclassmembers  class android.util.Vendor  { <fields>; }


-keep class android.hardware.usb.** {*;}
-keepnames class android.hardware.usb.** {*;}
-keepclassmembers class android.hardware.usb.** {*;}

-keep class com.dspread.xpos.** {*;}
-keepnames class com.dspread.xpos.** { *; }
-keepclassmembers class com.dspread.xpos.** { *; }
-dontwarn com.dspread.xpos.**



-keep class com.pnsol.sdk.** { *; }
-dontwarn com.pnsol.sdk.**
-keep class com.pnsol.sdk.util** { *; }
-dontwarn com.pnsol.sdk.util**
-keep class org.bouncycastle.jce** { *; }
-dontwarn org.bouncycastle.jce**
-keep class org.bouncycastle.x509** { *; }
-dontwarn org.bouncycastle.x509**
-keep class com.example.** { *; }

-keep class net.fullcarga.** { *; }
-keep interface net.fullcarga.** { *; }

-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.**

-dontwarn com.itextpdf.text.**
-keep class com.itextpdf.text.** { *; }
### Fabric
# In order to provide the most meaningful crash reports
-keepattributes SourceFile,LineNumberTable
# If you're using custom Eception
-keep public class * extends java.lang.Exception

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

## Joda Time 2.3

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

-dontwarn org.jooq.**
-dontwarn org.jpos.**
-dontwarn org.jdom.**
-keep class org.jooq.** { *; }
-keep class org.jpos.** { *; }
-keep class org.jdom.** { *; }
-keep class org.codehaus.mojo** { *; }
-dontwarn org.codehaus.mojo**

##EventBus 3.0

-keepattributes Signature
-keepattributes *Annotation*


##Saripaar Input Validators

-keep class com.mobsandgeeks.saripaar.** { *; }

-keep class org.sqldroid.** { *; }

-keep class org.bouncycastle.** { *; }

-keep class com.pagatodoholdings.posandroid.secciones.retrofit.**{
public private *;
}

-keep class com.pagatodoholdings.posandroid.secciones.retrofit.RegistroBean {*;}
-keep class com.pagatodoholdings.posandroid.utils.ServiceSynVentasInteractor$* { *; }



# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions