package ru.cleverpumpkin.badge

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.internal.DefaultDomainObjectSet

class ProjectUtils {

    static TestedExtension getAndroidExtension(Project project) {
        def appExtension = project.extensions.findByType(AppExtension)
        if (appExtension != null) {
            return appExtension
        }
        def libraryExtension = project.extensions.findByType(LibraryExtension)
        if (libraryExtension != null) {
            return libraryExtension
        }
        throw new IllegalStateException(
                "Not an Android application; did you forget to apply application or library plugin?"
        )
    }

    static DefaultDomainObjectSet<BaseVariant> getAllVariants(TestedExtension extension) {
        if (extension instanceof LibraryExtension) {
            return ((LibraryExtension) extension).libraryVariants as DefaultDomainObjectSet<BaseVariant>
        }
        if (extension instanceof AppExtension) {
            return ((AppExtension) extension).applicationVariants as DefaultDomainObjectSet<BaseVariant>
        }
        throw new IllegalStateException(
                "Cannot retrive variants from extension: " + extension.class.simpleName
        )
    }

}
