package ru.cleverpumpkin.badge;

import javax.annotation.Nullable;

public class PluginExtension {

    public final String name;

    public int fontSize = 10;
    public boolean enabled = false;
    public String textColor = "#ffffff";
    public String labelColor = "#9C000000";
    @Nullable
    public String text = null;

    public PluginExtension(String name) {
        this.name = name;
    }

}
