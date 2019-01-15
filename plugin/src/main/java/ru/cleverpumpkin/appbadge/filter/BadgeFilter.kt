package ru.cleverpumpkin.appbadge.filter

import java.awt.image.BufferedImage

interface BadgeFilter {

    fun apply(image: BufferedImage)

}