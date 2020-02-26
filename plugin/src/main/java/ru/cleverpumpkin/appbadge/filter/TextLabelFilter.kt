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

    override fun apply(image: BufferedImage, isAdaptiveIcon: Boolean) {
        val imgWidth = image.width
        val imgHeight = image.height

        val fontSize = imgHeight / 60 * this.fontSize

        val graphics2D = (image.graphics as Graphics2D).apply {
            setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
            font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
        }

        val lineHeight = graphics2D.fontMetrics.height
        val padding = fontSize / 3

        val wrappedLines = TextWrappingUtils.wrap(text, graphics2D.fontMetrics, imgWidth)

        // Calculate height for text.
        // Maybe it will be better to calculate line height using ascent and descent
        // from FontMetrics, but im lazy.
        val textHeight = wrappedLines.fold(0, { acc, _ -> acc + lineHeight })
        
        // box around text, with padding on top/bottom
        val boxHeight = textHeight + 2 * padding
        val boxTop: Number
        val textTop: Number
        if (isAdaptiveIcon) {
            // icon is a circe; center text vertically
            boxTop = imgHeight / 2 - boxHeight / 2
            textTop = imgHeight / 2 - textHeight / 2
        } else {
            // icon is a square; position text on the bottom of the icon
            boxTop = imgHeight - boxHeight
            textTop = imgHeight - textHeight - padding
        }

        graphics2D.apply {
            // Draw label rectangle.
            color = labelColor
            fillRect(0, boxTop, imgWidth, boxHeight)
            color = textColor
        }

        // Draw lines from bottom.
        // yPoint specifies the top of the line to draw
        // drawString takes the "baseline" of the string
        val textBaselineOffset = lineHeight - graphics2D.fontMetrics.descent
        var yPoint = textTop
        for (i in wrappedLines.indices) {
            val wrappedLine = wrappedLines[i]
            val strWidth = graphics2D.fontMetrics.stringWidth(wrappedLine)

            // Center text if image width is larger than line width.
            val xPos = if (imgWidth >= strWidth) (imgWidth - strWidth) / 2 else 0
            
            graphics2D.drawString(wrappedLine, xPos, yPoint + textBaselineOffset)
            
            yPoint += lineHeight
        }

        graphics2D.dispose()
    }
}