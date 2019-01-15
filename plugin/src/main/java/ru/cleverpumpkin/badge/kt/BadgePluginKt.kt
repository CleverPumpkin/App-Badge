package ru.cleverpumpkin.badge.kt

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import ru.cleverpumpkin.badge.ResourceUtils
import ru.cleverpumpkin.badge.filter.BadgeFilter
import ru.cleverpumpkin.badge.filter.TextLabelFilter
import java.io.File
import java.util.Objects.requireNonNull

/**
 * @author Sergey Chuprin
 */
class BadgePluginKt : Plugin<Project> {

    private companion object {
        private const val awtToolkitProp = "awt.toolkit"
        private const val awtGraphicsProp = "java.awt.graphicsenv"
    }

    init {
        System.setProperty("java.awt.headless", "true")
        try {
            Class.forName(System.getProperty(awtGraphicsProp))
        } catch (e: ClassNotFoundException) {
            System.err.println("[WARN] $awtGraphicsProp: $e")
            System.setProperty(awtGraphicsProp, "sun.awt.CGraphicsEnvironment")
        }

        try {
            Class.forName(System.getProperty(awtToolkitProp))
        } catch (e: ClassNotFoundException) {
            System.err.println("[WARN] $awtToolkitProp: $e")
            System.setProperty(awtToolkitProp, "sun.lwawt.macosx.LWCToolkit")
        }
    }

    override fun apply(project: Project) {
        project.extensions.add(BadgeExtensionKt.NAME, BadgeExtensionKt::class.java)

        val variants = getConfigContainer("variants", project)
        val buildTypes = getConfigContainer("buildTypes", project)
        val productFlavors = getConfigContainer("productFlavors", project)

        project.afterEvaluate {
            val androidExtension = ProjectUtilsKt.getAndroidExtension(project)

            val extension = project.extensions.getByType(BadgeExtensionKt::class.java)
            val tasks = mutableListOf<Task>()

            val allVariants = ProjectUtilsKt.getAllVariants(androidExtension)

            allVariants.forEach { variant ->
                val configs = getAllConfigs(variant, variants, buildTypes, productFlavors)
                var enabled = true

                configs.forEach { config -> enabled = enabled && config.enabled }

                if (enabled) {
                    val filters = getAllFilters(configs, variant)

                    if (filters.isNotEmpty()) {
                        val badgeTask =
                            createBadgeTask(filters, extension, androidExtension, project, variant)
                        tasks.add(badgeTask)

                        val androidResTasks = project.getTasksByName(
                            "generate${variant.name.capitalize()}Resources",
                            false
                        )
                        androidResTasks.forEach { task -> task.dependsOn(badgeTask) }
                    }
                }
            }
        }
    }

    private fun createBadgeTask(
        filters: List<BadgeFilter>,
        extension: BadgeExtensionKt,
        android: BaseExtension,
        project: Project,
        variant: BaseVariant
    ): BadgeTaskKt {
        val generatedResDir = getGeneratedResDir(project, variant)
        android.sourceSets.getByName(variant.name).res.srcDir(generatedResDir)

        val taskName = "generateBadgeFor${variant.name.capitalize()}"
        return project.tasks.create(
            taskName,
            BadgeTaskKt::class.java,
            variant,
            generatedResDir,
            extension.iconNames,
            filters
        )
    }

    private fun getGeneratedResDir(project: Project, variant: BaseVariant): File {
        return File(project.buildDir, "generated/badge/res/${variant.name}")
    }

    private fun getAllFilters(
        configs: List<PluginExtensionKt>,
        variant: BaseVariant
    ): List<BadgeFilter> {
        return configs.map { config -> createFilter(config, variant) }
    }

    private fun createFilter(config: PluginExtensionKt, variant: BaseVariant): TextLabelFilter {
        val text = requireNonNull(config.text) {
            "PluginExtension's text is null for variant: " + variant.buildType.name
        }

        val textColor = ResourceUtils.parseColor(config.textColor)
        val labelColor = ResourceUtils.parseColor(config.labelColor)

        return TextLabelFilter(text, textColor, labelColor, config.fontSize)
    }

    private fun getAllConfigs(
        variant: BaseVariant,
        variants: NamedDomainObjectContainer<PluginExtensionKt>,
        buildTypes: NamedDomainObjectContainer<PluginExtensionKt>,
        productFlavors: NamedDomainObjectContainer<PluginExtensionKt>
    ): List<PluginExtensionKt> {

        val configs = mutableListOf<PluginExtensionKt>()

        // Variants has the highest priority,
        // do not add params from other configs if variants were specified.

        variants.forEach { if (variant.name == it.name) configs.add(it) }

        if (configs.isEmpty()) {
            productFlavors.forEach { if (variant.flavorName == it.name) configs.add(it) }
            buildTypes.forEach { if (variant.buildType.name == it.name) configs.add(it) }
        }
        return configs
    }


    private fun getConfigContainer(
        name: String,
        project: Project
    ): NamedDomainObjectContainer<PluginExtensionKt> {
        val container = project.container(PluginExtensionKt::class.java)
        return container.also { project.extensions.add(name, it) }
    }

}