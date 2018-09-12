package ru.cleverpumpkin.badge

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import ru.cleverpumpkin.badge.filter.BadgeFilter
import ru.cleverpumpkin.badge.filter.TextLabelFilter

import java.awt.*
import java.util.List

import static java.util.Objects.requireNonNull

@CompileStatic
@SuppressWarnings("SpellCheckingInspection")
class BadgePlugin implements Plugin<Project> {

    private static final String awtToolkitProp = "awt.toolkit"
    private static final String awtGraphicsProp = "java.awt.graphicsenv"

    static {

        System.setProperty("java.awt.headless", "true")

        // workaround for an Android Studio issue
        try {
            Class.forName(System.getProperty(awtGraphicsProp))
        } catch (ClassNotFoundException e) {
            System.err.println("[WARN] " + awtGraphicsProp + ": " + e)
            System.setProperty(awtGraphicsProp, "sun.awt.CGraphicsEnvironment")
        }

        try {
            Class.forName(System.getProperty(awtToolkitProp))
        } catch (ClassNotFoundException e) {
            System.err.println("[WARN] " + awtToolkitProp + ": " + e)
            System.setProperty(awtToolkitProp, "sun.lwawt.macosx.LWCToolkit")
        }
    }

    @Override
    void apply(Project project) {

        project.extensions.add BadgeExtension.NAME, BadgeExtension

        //these blocks will be available for configuration
        NamedDomainObjectContainer<PluginExtension> variants = getConfigContainer('variants', project)
        NamedDomainObjectContainer<PluginExtension> buildTypes = getConfigContainer('buildTypes', project)
        NamedDomainObjectContainer<PluginExtension> productFlavors = getConfigContainer('productFlavors', project)

        project.afterEvaluate {

            AppExtension android = getAndroidExtension(project)

            BadgeExtension extension = project.extensions.findByType(BadgeExtension)
            List<Task> tasks = new ArrayList<Task>()

            android.applicationVariants.all { ApplicationVariant variant ->

                List<PluginExtension> configs = getAllConfigs(variant, variants, buildTypes, productFlavors)

                boolean enabled = true
                configs.each { enabled = enabled && it.enabled }

                if (enabled) {

                    List<BadgeFilter> filters = getAllFilters(configs, variant)

                    BadgeTask badgeTask = createBadgeTask(filters, extension, android, project, variant)
                    tasks.add(badgeTask)

                    project
                            .getTasksByName("generate${variant.name.capitalize()}Resources", false)
                            .forEach { Task task -> task.dependsOn(badgeTask) }
                }
            }

            project.task(BadgeTask.NAME, dependsOn: tasks)
        }
    }

    private static List<BadgeFilter> getAllFilters(
            List<PluginExtension> configs,
            ApplicationVariant variant
    ) {
        List<BadgeFilter> filters = new ArrayList<>()
        configs.each { PluginExtension config -> filters.add(createFilter(config, variant)) }
        filters
    }

    private static List<PluginExtension> getAllConfigs(
            ApplicationVariant variant,
            NamedDomainObjectContainer<PluginExtension> variants,
            NamedDomainObjectContainer<PluginExtension> buildTypes,
            NamedDomainObjectContainer<PluginExtension> productFlavors
    ) {
        List<PluginExtension> configs = new ArrayList<>()

        //variants has the highest priority;
        // do not add params from other configs if variants were specified

        variants.each { if (variant.name == it.name) configs.add(it) }

        if (configs.isEmpty()) {
            productFlavors.each { if (variant.flavorName == it.name) configs.add(it) }
            buildTypes.each { if (variant.buildType.name == it.name) configs.add(it) }
        }
        configs
    }

    private static BadgeTask createBadgeTask(
            List<BadgeFilter> filters,
            BadgeExtension extension,
            AppExtension android,
            Project project,
            ApplicationVariant variant
    ) {
        File generatedResDir = getGeneratedResDir(project, variant)
        android.sourceSets.findByName(variant.name).res.srcDir(generatedResDir)

        GString name = "${BadgeTask.NAME}${variant.name.capitalize()}"
        BadgeTask task = project.task(name, type: BadgeTask) as BadgeTask

        task.config(
                variant,
                generatedResDir,
                new HashSet<String>(extension.iconNames),
                new HashSet<String>(extension.foregroundIconNames),
                filters
        )
        task
    }

    private
    static TextLabelFilter createFilter(PluginExtension config, ApplicationVariant variant) {
        String text = requireNonNull(config.text, {
            "PluginExtension's text is null for variant: " + variant.buildType.name
        })

        Color textColor = Resources.parseColor(config.textColor)
        Color labelColor = Resources.parseColor(config.labelColor)

        new TextLabelFilter(text, textColor, labelColor, config.fontSize)
    }

    private static AppExtension getAndroidExtension(Project project) {
        requireNonNull(project.extensions.findByType(AppExtension), {
            "Not an Android application; did you forget `apply plugin: 'com.android.application`?"
        })
    }

    private static NamedDomainObjectContainer<PluginExtension> getConfigContainer(
            String name,
            Project project
    ) {
        NamedDomainObjectContainer<PluginExtension> container = project.container(PluginExtension)
        project.extensions.add(name, container)
        container
    }

    private static File getGeneratedResDir(Project project, ApplicationVariant variant) {
        new File(project.buildDir, "generated/badge/res/${variant.name}")
    }
}
