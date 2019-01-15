package ru.cleverpumpkin.appbadge

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import ru.cleverpumpkin.appbadge.extension.PluginExtension
import ru.cleverpumpkin.appbadge.extension.VariantExtension
import ru.cleverpumpkin.appbadge.filter.AppBadgeFilter
import ru.cleverpumpkin.appbadge.filter.TextLabelFilter
import ru.cleverpumpkin.appbadge.utils.ProjectUtils.getAllVariants
import ru.cleverpumpkin.appbadge.utils.ProjectUtils.getAndroidExtension
import ru.cleverpumpkin.appbadge.utils.ResourceUtils
import java.io.File

/**
 * @author Sergey Chuprin
 */
@Suppress("unused")
class AppBadgePlugin : Plugin<Project> {

    init {
        System.setProperty("java.awt.headless", "true")
        try {
            Class.forName(System.getProperty("java.awt.graphicsenv"))
        } catch (e: ClassNotFoundException) {
            System.err.println("[WARN] java.awt.graphicsenv: $e")
            System.setProperty("java.awt.graphicsenv", "sun.awt.CGraphicsEnvironment")
        }

        try {
            Class.forName(System.getProperty("awt.toolkit"))
        } catch (e: ClassNotFoundException) {
            System.err.println("[WARN] awt.toolkit: $e")
            System.setProperty("awt.toolkit", "sun.lwawt.macosx.LWCToolkit")
        }
    }

    override fun apply(project: Project) {
        with(project) {
            extensions.add(PluginExtension.NAME, PluginExtension::class.java)

            val flavors = createVariantExtension(PluginExtension.FLAVORS)
            val variants = createVariantExtension(PluginExtension.VARIANTS)
            val buildTypes = createVariantExtension(PluginExtension.BUILD_TYPES)

            afterEvaluate {

                val pluginExtension = getPluginExtension()
                val androidExtension = getAndroidExtension(this)

                getAllVariants(androidExtension)
                    .forEach { currentVariant ->

                        val extensions = getVariantExtensions(
                            currentVariant = currentVariant,
                            variants = variants,
                            buildTypes = buildTypes,
                            productFlavors = flavors
                        )

                        if (extensions.any(VariantExtension::enabled)) {
                            hookAndroidTasks(
                                variantExtensions = extensions,
                                currentVariant = currentVariant,
                                pluginExtension = pluginExtension,
                                androidExtension = androidExtension
                            )
                        }
                    }
            }
        }
    }

    private fun Project.hookAndroidTasks(
        pluginExtension: PluginExtension,
        androidExtension: TestedExtension,
        variantExtensions: List<VariantExtension>,
        currentVariant: BaseVariant
    ) {
        val filters = variantExtensions
            .map(::createFilter)
            .takeUnless(List<TextLabelFilter>::isEmpty)
            ?: return

        val taskName = "generate${currentVariant.name.capitalize()}Resources"
        val androidResTasks = getTasksByName(taskName, false)
            .takeUnless(MutableSet<Task>::isEmpty)
            ?: return

        val badgeTask = createBadgeTask(
            filters = filters,
            currentVariant = currentVariant,
            pluginExtension = pluginExtension,
            androidExtension = androidExtension
        )

        androidResTasks.forEach { task -> task.dependsOn(badgeTask) }
    }

    private fun Project.createBadgeTask(
        currentVariant: BaseVariant,
        filters: List<AppBadgeFilter>,
        androidExtension: BaseExtension,
        pluginExtension: PluginExtension
    ): GenerateIconsTask {
        val generatedResDir = getGeneratedResDir(currentVariant)

        // Make generated icons visible in variant's resources.
        androidExtension.sourceSets.getByName(currentVariant.name).res.srcDir(generatedResDir)

        val taskName = "generateBadgeFor${currentVariant.name.capitalize()}"
        return tasks.create(
            taskName,
            GenerateIconsTask::class.java,
            currentVariant,
            generatedResDir,
            pluginExtension.iconNames,
            filters
        )
    }

    private fun createFilter(config: VariantExtension): TextLabelFilter {
        val textColor = ResourceUtils.parseColor(config.textColor)
        val labelColor = ResourceUtils.parseColor(config.labelColor)
        return TextLabelFilter(config.text, config.fontSize, textColor, labelColor)
    }

    private fun getVariantExtensions(
        currentVariant: BaseVariant,
        variants: NamedDomainObjectContainer<VariantExtension>,
        buildTypes: NamedDomainObjectContainer<VariantExtension>,
        productFlavors: NamedDomainObjectContainer<VariantExtension>
    ): List<VariantExtension> {

        // Variants has highest priority.
        // Check variants first and do not add params from other configs if variants were specified.
        return variants
            .fold(mutableListOf<VariantExtension>()) { acc, extension ->
                acc.apply { if (currentVariant.name == extension.name) add(extension) }
            }
            .apply {
                if (isEmpty()) {
                    productFlavors.forEach { extension ->
                        if (currentVariant.flavorName == extension.name) add(extension)
                    }
                    buildTypes.forEach { extension ->
                        if (currentVariant.buildType.name == extension.name) add(extension)
                    }
                }
            }
    }

    private fun Project.createVariantExtension(
        extensionName: String
    ): NamedDomainObjectContainer<VariantExtension> {
        val container = container(VariantExtension::class.java)
        return container.also { extensions.add(extensionName, it) }
    }

    private fun Project.getPluginExtension(): PluginExtension {
        return extensions.getByType(PluginExtension::class.java)
    }

    private fun Project.getGeneratedResDir(variant: BaseVariant): File {
        return File(buildDir, "generated/badge/res/${variant.name}")
    }

}