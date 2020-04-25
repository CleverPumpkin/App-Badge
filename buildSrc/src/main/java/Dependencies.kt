object Versions {
    const val kotlin = "1.3.11"
    const val androidX = "1.0.0"
    const val gradlePlugin = "3.3.0"
    const val projectVersion = "1.0.3"
}

object BuildScriptPlugins {
    const val appBadge = "app-badge:plugin:${Versions.projectVersion}"
    const val android = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Plugins {
    const val appBadge = "ru.cleverpumpkin.badge"
}

object Libraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val androidX = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.androidX}"
}