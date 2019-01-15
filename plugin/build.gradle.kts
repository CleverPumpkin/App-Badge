plugins {
    id("com.gradle.plugin-publish") version "0.10.0"
    `kotlin-dsl`
    maven
}

repositories {
    jcenter()
    google()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(BuildScriptPlugins.android)
}

group = Plugins.appBadge

// Upload archive to rootProject/plugin/badgeRepo folder to test plugin locale.
tasks.named<Upload>("uploadArchives") {
    repositories.withGroovyBuilder {
        "mavenDeployer" {
            "repository"("url" to "file://badgeRepo")
        }
    }
}

// Add info for publication to plugin portal.
pluginBundle {
    vcsUrl = "https://github.com/CleverPumpkin/App-Badge"
    website = "https://github.com/CleverPumpkin/App-Badge"
    description = "This is an Android gradle plugin that allows you to overlay " +
            "text on top of an android application\'s icon"
    tags = listOf("android", "icon", "generator", "badge", "label", "version")
}

// Create plugin itself.
gradlePlugin {
    plugins {
        create("appBadgePlugin") {
            id = Plugins.appBadge
            displayName = "App Badge Generator"
            version = Versions.projectVersion
            implementationClass = "ru.cleverpumpkin.appbadge.AppBadgePlugin"
        }
    }
}