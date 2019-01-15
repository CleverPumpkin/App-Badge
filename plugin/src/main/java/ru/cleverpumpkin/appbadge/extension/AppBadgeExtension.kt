package ru.cleverpumpkin.appbadge.extension

/**
 * @author Sergey Chuprin
 */
open class AppBadgeExtension {

    companion object {
        const val NAME = "appBadge"
    }

    // `iconNames` includes: "@drawable/icon", "@mipmap/ic_launcher", etc.
    var iconNames = listOf<String>()

}