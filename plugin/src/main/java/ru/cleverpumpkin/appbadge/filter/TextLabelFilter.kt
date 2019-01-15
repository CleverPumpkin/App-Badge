package ru.cleverpumpkin.appbadge.filter

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_TEXT_ANTIALIASING
import java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
import java.awt.image.BufferedImage

/**
 * @author Sergey Chuprin
 */
class TextLabelFilter(
    private val text: String,
    private val fontSize: Int,
    private val textColor: Color,
    private val labelColor: Color
) : AppBadgeFilter {

    override fun apply(image: BufferedImage) {
        val imgWidth = image.width
        val imgHeight = image.height

        val fontSize = imgHeight / 60 * this.fontSize

        val graphics2D = (image.graphics as Graphics2D).apply {
            setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
            font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
        }

        val lineHeight = graphics2D.fontMetrics.height
        val padding = fontSize / 3

        var yPoint = imgHeight - padding

        val wrappedLines = TextWrappingUtils.wrap(text, graphics2D.fontMetrics, imgWidth)

        // Calculate most bottom Y coordinate.
        // Maybe it will be better to calculate line height using ascent and descent
        // from FontMetrics, but im lazy.
        val endYPoint = wrappedLines.fold(yPoint) { acc, _ -> acc - lineHeight / 2 }

        graphics2D.apply {
            // Draw label rectangle.
            color = labelColor
            fillRect(0, endYPoint - padding * 2, imgWidth, yPoint)
            color = textColor
        }

        // Draw lines from bottom.
        for (i in wrappedLines.indices.reversed()) {
            val wrappedLine = wrappedLines[i]
            val strWidth = graphics2D.fontMetrics.stringWidth(wrappedLine)

            // Center text if image width is larger than line width.
            val xPos = if (imgWidth >= strWidth) (imgWidth - strWidth) / 2 else 0

            graphics2D.drawString(wrappedLine, xPos, yPoint)

            yPoint -= lineHeight / 2
        }

        graphics2D.dispose()
    }
}