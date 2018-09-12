package ru.cleverpumpkin.badge;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ru.cleverpumpkin.badge.filter.BadgeFilter;

public class ImageWriter {

    private final File outputFile;

    private final BufferedImage image;

    public ImageWriter(File inputFile, File outputFile) throws IOException {
        this.outputFile = outputFile;
        image = ImageIO.read(inputFile);
    }

    public void write() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        outputFile.getParentFile().mkdirs();
        ImageIO.write(image, "png", outputFile);
    }

    public void process(Stream<BadgeFilter> filters) {
        filters.forEach(filter -> filter.apply(image));
    }
}