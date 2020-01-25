package ru.cleverpumpkin.appbadge.utils

import ru.cleverpumpkin.appbadge.filter.AppBadgeFilter
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class ImageWriter(private val outputFile: File) {

    private var image: BufferedImage? = null

    @Throws(IOException::class)
    fun read(inputFile: File) {
        image = ImageIO.read(inputFile)
    }

    @Throws(IOException::class)
    fun write() {
        outputFile.parentFile.mkdirs()
        ImageIO.write(image, "png", outputFile)
    }

    fun process(filters: List<AppBadgeFilter>, isAdaptive: Boolean) {
        if (image == null) {
            // start with empty transparent image
            image = BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB)
        }
        image?.let { image ->
            filters.forEach { filter -> filter.apply(image, isAdaptive) }
        }
    }
}