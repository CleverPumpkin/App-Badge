package ru.cleverpumpkin.appbadge.extension

class PluginExtension(
    val name: String
) {
    var fontSize = 10
    var enabled = false
    var textColor = "#FFFFFF"
    var labelColor = "#9C000000"
    var text: String? = null
}
