package ru.cleverpumpkin.badge.kt


import ru.cleverpumpkin.badge.filter.BadgeFilter
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.stream.Stream
import javax.imageio.ImageIO

class ImageWriterKt(inputFile: File, private val outputFile: File) {

    private val image: BufferedImage = ImageIO.read(inputFile)

    @Throws(IOException::class)
    fun write() {
        outputFile.parentFile.mkdirs()
        ImageIO.write(image, "png", outputFile)
    }

    fun process(filters: Stream<BadgeFilter>) = filters.forEach { filter -> filter.apply(image) }
}