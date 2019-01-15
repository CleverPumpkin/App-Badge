plugins {
    id("com.android.library")
    id("kotlin-android")
//    id("ru.cleverpumpkin.badge")
}

//badge {
//    buildTypes {
//        create("debug") {
//            enabled = true
//            text = "1.0.1"
//        }
//    }
//}

dependencies {
    implementation(Libraries.kotlin)
    implementation(Libraries.androidX)
}
