package ru.cleverpumpkin.badge;

import org.xml.sax.SAXException;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;

import static java.util.Collections.unmodifiableList;

public class ResourceUtils {

    private static XmlSlurper xmlSlurper;

    static {
        try {
            xmlSlurper = new XmlSlurper();
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    public static String resourceFilePattern(String name) {
        if (name.startsWith("@")) {
            String[] pair = name.substring(1).split("/", 2);
            String baseResType = pair[0];
            String fileName = Objects.requireNonNull(pair[1], () ->
                    "Icon names does include resource types (e.g. drawable/ic_launcher):" + name
            );
            return baseResType + "*/" + fileName + ".*";
        }
        return name;

    }

    public static List<String> getLauncherIcons(File manifestFile) throws SAXException, IOException {

        GPathResult manifestXml = xmlSlurper.parse(manifestFile);
        GPathResult applicationNode = (GPathResult) manifestXml.getProperty("application");

        String icon = String.valueOf(applicationNode.getProperty("@android:icon"));
        String roundIcon = String.valueOf(applicationNode.getProperty("@android:roundIcon"));

        List<String> icons = new ArrayList<>(2);
        if (!icon.isEmpty()) icons.add(icon);
        if (!roundIcon.isEmpty()) icons.add(roundIcon);

        return unmodifiableList(icons);
    }

    /**
     * </p>Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:</p>
     *
     * <ul>
     * <li><code>#RRGGBB</code></li>
     * <li><code>#AARRGGBB</code></li>
     * </ul>
     */
    public static Color parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return new Color((int) color, true);
        }
        throw new IllegalArgumentException("Unknown color: " + colorString);
    }
}
