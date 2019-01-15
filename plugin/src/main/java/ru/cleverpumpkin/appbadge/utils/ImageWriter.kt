package ru.cleverpumpkin.appbadge.utils


import ru.cleverpumpkin.appbadge.filter.AppBadgeFilter
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class ImageWriter(inputFile: File, private val outputFile: File) {

    private val image: BufferedImage = ImageIO.read(inputFile)

    @Throws(IOException::class)
    fun write() {
        outputFile.parentFile.mkdirs()
        ImageIO.write(image, "png", outputFile)
    }

    fun process(filters: List<AppBadgeFilter>) = filters.forEach { filter -> filter.apply(image) }

}