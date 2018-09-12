package ru.cleverpumpkin.badge

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskAction
import ru.cleverpumpkin.badge.filter.BadgeFilter

import java.util.stream.Collectors
import java.util.stream.Stream

class BadgeTask extends DefaultTask {

    public static final String NAME = "badge"

    private ApplicationVariant variant

    //@OutputDirectory
    File outputDir

    // `iconNames` includes: "@drawable/icon", "@mipmap/ic_launcher", etc.
    Set<String> iconNames
    Set<String> foregroundIconNames

    List<BadgeFilter> filters = []

    void config(
            ApplicationVariant variant,
            File outputDir,
            Set<String> iconNames,
            Set<String> foregroundIconNames,
            List<BadgeFilter> filters
    ) {
        this.filters = filters
        this.variant = variant
        this.outputDir = outputDir
        this.iconNames = iconNames
        this.foregroundIconNames = foregroundIconNames
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @TaskAction
    def run() {

        if (filters.empty) return

        long currentTime = System.currentTimeMillis()

        Set<String> allIcons = getAllIcons()

        Stream<File> resDirectories = variant
                .sourceSets
                .stream()
                .flatMap({ sourceProvider -> sourceProvider.resDirectories.stream() })

        resDirectories.forEach { File resDir ->

            if (resDir == outputDir) return

            allIcons.forEach { String name ->

                getResourcesFileTree(resDir, name).forEach { File inputFile ->
                    processIcon(inputFile)
                }
            }
        }

        logInfo("task finished in ${System.currentTimeMillis() - currentTime}ms")
    }

    Set<String> getAllIcons() {
        def allIcons = new HashSet<String>(iconNames)
        allIcons.addAll(launcherIconNames)
        allIcons.addAll(foregroundIconNames)
        allIcons
    }

    def processIcon(File inputFile) {

        logInfo "process $inputFile"

        def basename = inputFile.name
        def resType = inputFile.parentFile.name
        def outputFile = new File(outputDir, "${resType}/${basename}")
        outputFile.parentFile.mkdirs()

        def imageWriter = new ImageWriter(inputFile, outputFile)
        imageWriter.process(filters.stream())
        imageWriter.write()
    }

    ConfigurableFileTree getResourcesFileTree(File resDir, String name) {
        project.fileTree(
                dir: resDir,
                include: Resources.resourceFilePattern(name),
                exclude: "**/*.xml",
        )
    }

    def logInfo(String message) {
        project.logger.info "[$name] $message"
    }

    Set<String> getLauncherIconNames() {
        androidManifestFiles
                .flatMap { File file -> Resources.getLauncherIcons(file).stream() }
                .collect(Collectors.toSet())
    }

    Stream<File> getAndroidManifestFiles() {
        AppExtension android = project.extensions.findByType(AppExtension)
        ["main", variant.name, variant.buildType.name, variant.flavorName]
                .stream()
                .filter({ name -> !name.empty })
                .distinct()
                .map { name -> project.file(android.sourceSets[name].manifest.srcFile) }
                .filter { manifestFile -> manifestFile.exists() }
    }
}