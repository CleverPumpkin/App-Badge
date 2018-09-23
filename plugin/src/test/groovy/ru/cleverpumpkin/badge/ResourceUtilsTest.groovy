package ru.cleverpumpkin.badge

import spock.lang.Specification

class ResourceUtilsTest extends Specification {
    def "resourceFilePattern"() {
        expect:
        ResourceUtils.resourceFilePattern(resName) == pattern

        where:
        resName                 | pattern
        "@drawable/ic_launcher" | "drawable*/ic_launcher.*"
        "@mipmap/icon"          | "mipmap*/icon.*"
    }

    def "getLauncherIcon without android:roundIcon"() {
        setup:
        def file = File.createTempFile("AndroidManifest", ".xml")
        file.deleteOnExit()
        file.write('''
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="ru.cleverpumpkin.badge.example"
    android:versionCode="1"
    android:versionName="1.0" >
    <uses-sdk
        android:minSdkVersion="15"
        android:targetSdkVersion="23" />
    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:text="@string/app_name"
        android:theme="@style/AppTheme" >
    </application>
</manifest>
'''.trim())

        expect:
        ResourceUtils.getLauncherIcons(file).containsAll(["@drawable/ic_launcher"])
    }

    def "getLauncherIcon without android:icon"() {
        setup:
        def file = File.createTempFile("AndroidManifest", ".xml")
        file.deleteOnExit()
        file.write('''
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="ru.cleverpumpkin.badge.example"
    android:versionCode="1"
    android:versionName="1.0" >
    <uses-sdk
        android:minSdkVersion="15"
        android:targetSdkVersion="23" />
    <application
        android:allowBackup="true"
        android:roundIcon="@drawable/ic_launcher_round"
        android:text="@string/app_name"
        android:theme="@style/AppTheme" >
    </application>
</manifest>
'''.trim())

        expect:
        ResourceUtils.getLauncherIcons(file).containsAll(["@drawable/ic_launcher_round"])
    }

    def "getLauncherIcon with both android:icon and android:roundIcon"() {
        setup:
        def file = File.createTempFile("AndroidManifest", ".xml")
        file.deleteOnExit()
        file.write('''
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="ru.cleverpumpkin.badge.example"
    android:versionCode="1"
    android:versionName="1.0" >
    <uses-sdk
        android:minSdkVersion="15"
        android:targetSdkVersion="23" />
    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:roundIcon="@drawable/ic_launcher_round"
        android:text="@string/app_name"
        android:theme="@style/AppTheme" >
    </application>
</manifest>
'''.trim())

        expect:
        ResourceUtils.getLauncherIcons(file).containsAll(["@drawable/ic_launcher", "@drawable/ic_launcher_round"])
    }
}
