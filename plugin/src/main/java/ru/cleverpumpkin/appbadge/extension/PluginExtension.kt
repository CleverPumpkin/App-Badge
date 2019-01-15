package ru.cleverpumpkin.appbadge.extension

/**
 * @author Sergey Chuprin
 */
open class PluginExtension {

    companion object {
        const val NAME = "appBadge"

        const val VARIANTS = "variants"
        const val FLAVORS = "productFlavors"
        const val BUILD_TYPES = "buildTypes"
    }

    // `iconNames` includes: "@drawable/icon", "@mipmap/ic_launcher", etc.
    var iconNames = listOf<String>()

}