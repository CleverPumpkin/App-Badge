package ru.cleverpumpkin.badge.filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

public class TextLabelFilter implements BadgeFilter {

    private final int fontSize;

    private final Color textColor;

    private final Color labelColor;

    private final String text;

    public TextLabelFilter(String text, Color textColor, Color labelColor, int fontSize) {
        this.text = text;
        this.fontSize = fontSize;
        this.textColor = textColor;
        this.labelColor = labelColor;
    }

    @Override
    public void apply(BufferedImage image) {

        final int imgWidth = image.getWidth();
        final int imgHeight = image.getHeight();

        final int fontSize = (imgHeight / 60) * this.fontSize;

        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));

        final int lineHeight = g.getFontMetrics().getHeight();
        final int padding = fontSize / 3;

        int yPoint = imgHeight - padding;

        List<String> wrappedLines = TextWrappingUtils.wrap(text, g.getFontMetrics(), imgWidth);

        int endYPoint = yPoint;
        for (int i = 0; i < wrappedLines.size(); i++) {
            endYPoint -= lineHeight / 2;
        }

        g.setColor(labelColor);
        g.fillRect(0, endYPoint - (padding * 2), imgWidth, yPoint);

        g.setColor(textColor);

        for (int i = wrappedLines.size() - 1; i >= 0; i--) {

            String wrappedLine = wrappedLines.get(i);

            final int strWidth = g.getFontMetrics().stringWidth(wrappedLine);

            int x = 0;
            if (imgWidth >= strWidth) x = ((imgWidth - strWidth) / 2);

            g.drawString(wrappedLine, x, yPoint);

            yPoint -= lineHeight / 2;
        }

        g.dispose();
    }
}
