package ru.cleverpumpkin.appbadge.filter

import java.awt.image.BufferedImage

/**
 * @author Sergey Chuprin
 */
interface AppBadgeFilter {

    fun apply(image: BufferedImage, isAdaptiveIcon: Boolean)

}