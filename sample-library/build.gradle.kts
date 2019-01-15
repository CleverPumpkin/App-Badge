plugins {
    id("com.android.library")
    id("kotlin-android")
    id(Plugins.appBadge)
}

appBadge {
    iconNames = listOf("@mipmap/ic_lib_launcher")
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
}