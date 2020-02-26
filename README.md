Gradle Plugin for adding a badge with version to app icons

![alt text](img/ic_launcher_round.png)

# Compatibility
Gradle 5.1.1

Android Gradle Plugin 3.3.
# How to add
Add this to your project's **build.gradle**
```
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath "gradle.plugin.app-badge:plugin:1.0.3"
    }
}
```
Then apply plugin in your app's **build.gradle**
```
apply plugin: "ru.cleverpumpkin.badge"
```

# Configuration
```
android {
    buildTypes {
        debug {}
        release {}
    }
    productFlavors {
        stage {}
        production {}
    }
}

badge {
    buildTypes {
        debug {
            enabled = true
            text = "debug"
        }
        // Do not add badge in release build type.
    }
    // or
    productFlavors {
        stage {
            enabled = true
            text = "stage"
        }

        production {
            enabled = true
            text = "production"
        }
    }
    // or
    // Variants has the highest priority. If variants config specified,
    // others will be ignored.
    variants {
        stageDebug {
            enabled = true
            text = "stageDebug"
        }
    }
}
```
### Custom icons
You can specify manually which icons to process:
```
badge {
    iconNames = ["@mipmap/ic_launcher_cusom"]
}
```

### Note
If you're using plugin in a library module and use icons from this
module in you app module, you need to specify icon names in library
module.

## Styling
You can specify text size, label color, text color.
Gravity customization isn't available. Bottom used by default.
```
badge {
    buildTypes {
        debug {
            enabled = true
            text = "debug"
            fontSize = 12 // Default value: 10
            
            // Note that colors in format "#XXX" not supported,
            // you have to specify colors as "#XXXXXX".
            textColor = "#FFFFFF"
            labelColor = "#000000"
        }
    }
}
```

# Developed by 
Sergey Chuprin - <gregamer@gmail.com>
# Maintained by
CleverPumpkin – https://cleverpumpkin.ru

https://plugins.gradle.org/plugin/ru.cleverpumpkin.badge
