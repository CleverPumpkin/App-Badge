package ru.cleverpumpkin.appbadge.utils

import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import org.xml.sax.SAXException
import java.awt.Color
import java.io.File
import java.io.IOException

/**
 * @author Sergey Chuprin
 */
object ResourceUtils {

    private const val MANIFEST_ICON_PROPERTY = "@android:icon"
    private const val MANIFEST_APPLICATION_PROPERTY = "application"
    private const val MANIFEST_ROUND_ICON_PROPERTY = "@android:roundIcon"

    private val xmlSlurper = XmlSlurper()

    @JvmStatic
    fun resourceFilePattern(name: String): String {
        if (!name.startsWith("@")) {
            return name
        }
        val expectedPair = name.substring(1).split("/".toRegex(), 2)
        val resType = expectedPair.first()
        val filename = requireNotNull(expectedPair.getOrNull(1)) {
            "Icon names does include resource types (e.g. drawable/ic_launcher):$name"
        }
        return "$resType*/$filename.*"
    }

    @JvmStatic
    @Throws(SAXException::class, IOException::class)
    fun getLauncherIcons(manifestFile: File): List<String> {
        val applicationNode = xmlSlurper
            .parse(manifestFile)
            .getProperty(MANIFEST_APPLICATION_PROPERTY) as GPathResult

        val icon = applicationNode.getProperty(MANIFEST_ICON_PROPERTY).toString()
        val roundIcon = applicationNode.getProperty(MANIFEST_ROUND_ICON_PROPERTY).toString()

        return listOfNotNull(icon.takeIf(String::isNotEmpty), roundIcon.takeIf(String::isNotEmpty))
    }

    /**
     * Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:
     * #RRGGBB
     * #AARRGGBB
     */
    @JvmStatic
    fun parseColor(colorString: String): Color {
        if (colorString[0] == '#') {

            // Use a long to avoid rollovers on #ffXXXXXX.
            val colorLong = colorString.substring(1).toLong(16)
            val resultColor = when {
                // Set alpha value.
                colorString.length == 7 -> colorLong or -0x1000000
                colorString.length != 9 -> {
                    throw IllegalArgumentException("Unknown color: $colorString")
                }
                else -> colorLong
            }
            return Color(resultColor.toInt(), true)
        }
        throw IllegalArgumentException("Unknown color: $colorString")
    }

}