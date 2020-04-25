plugins {
    id("com.android.application")
    id("kotlin-android")
    id(Plugins.appBadge)
}

android {
    defaultConfig {
        applicationId = "ru.cleverpumpkin.appbadge.sample"
    }
}

badge {
    iconNames = listOf(
        "@mipmap/ic_launcher_custom"
    )
    buildTypes {
        create("debug") {
            enabled = true
            text = Versions.projectVersion
        }
    }
}

dependencies {
    implementation(Libraries.kotlin)
    implementation(Libraries.androidX)
    implementation(project(":sample-library"))
}
