Gradle Plugin for adding a badge with version to app icons

![alt text](img/ic_launcher_round.png)

# How to add
Add this to your project's **build.gradle**
```
buildscript {
    repositories {
        maven { url "https://plugins.gradle.org/m2/" }
    }
    dependencies {
        classpath "gradle.plugin.app-badge:plugin:1.0.1"
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
        //do not add badge in release build type
    }

    //or

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

    //or
    //variants has the highest priority. If variants config specified,
    //others will be ignored

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
    buildTypes {
        debug {
            iconNames "@mipmap/ic_launcher_cusom", "@mipmap/ic_custom
            foregroundIconNames "@mipmap/ic_launcher_foreground"
            ...
        }
    }
}
```

### Note
If you're using plugin in a library module and use icons from this
module in you app module, you need to specify icon names in library
module

## Styling
You can specify text size, label color, text color.
Gravity customization isn't available. Bottom used by default

Default text size is 10

```
badge {
    buildTypes {
        debug {
            enabled = true
            text = "debug"
            fontSize = 12
            
            //note that colors in format "#xxx" not supported,
            //you have to specify colors as "#xxxxxx"
            textColor = "#ffffff"
            labelColor = "#000000"
        }
    }
}
```

# Developed by 
Sergey Chuprin - <gregamer@gmail.com>
