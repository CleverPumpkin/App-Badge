package ru.cleverpumpkin.badge.kt

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SourceProvider
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskAction
import ru.cleverpumpkin.badge.ResourceUtils
import ru.cleverpumpkin.badge.ResourceUtils.getLauncherIcons
import ru.cleverpumpkin.badge.filter.BadgeFilter
import java.io.File
import javax.inject.Inject

/**
 * @author Sergey Chuprin
 */
open class BadgeTaskKt @Inject constructor(
    private val variant: BaseVariant,
    // TODO: Add caching.
    private val outputDir: File,
    // `iconNames` includes: "@drawable/icon", "@mipmap/ic_launcher", etc.
    private val iconNames: Collection<String>,
    private val filters: List<BadgeFilter>
) : DefaultTask() {

    companion object {
        private const val MAIN_SOURCE_SET = "main"
        const val NAME = "badge"
    }

    @TaskAction
    fun run() {
        if (filters.isEmpty()) return
        val icons = getAllIcons().takeUnless(Set<String>::isEmpty) ?: return

        variant
            .sourceSets
            .flatMap(SourceProvider::getResDirectories)
            .forEach { resDir ->
                if (resDir == outputDir) return@forEach
                icons.forEach { name -> getResourcesFileTree(resDir, name).forEach(::processIcon) }
            }
    }

    private fun processIcon(inputFile: File) {
        val outputFile = File(outputDir, "${inputFile.parentFile.name}/${inputFile.name}")
        outputFile.parentFile.mkdirs()

        ImageWriterKt(inputFile, outputFile).run {
            process(filters.stream())
            write()
        }
    }

    private fun getResourcesFileTree(resDir: File, iconName: String): ConfigurableFileTree {
        return project.fileTree(resDir, {
            exclude("**/*.xml")
            include(ResourceUtils.resourceFilePattern(iconName))
        })
    }

    private fun getAllIcons(): Set<String> {
        return HashSet(iconNames).apply { addAll(getLauncherIconNames()) }
    }

    private fun getLauncherIconNames(): Set<String> {
        return getAndroidManifestFiles()
            .flatMap(::getLauncherIcons)
            .toSet()
    }

    private fun getAndroidManifestFiles(): List<File> {
        val androidExtension = ProjectUtilsKt.getAndroidExtension(project)
        return listOf(
            MAIN_SOURCE_SET,
            variant.name,
            variant.buildType.name,
            variant.flavorName
        ).mapNotNull { name ->
            when {
                name.isEmpty() -> null
                else -> {
                    val sourceSet = androidExtension.sourceSets.findByName(name)
                    sourceSet?.manifest?.srcFile?.let(project::file)?.takeIf(File::exists)
                }
            }
        }.distinctBy(File::getAbsolutePath)
    }

}