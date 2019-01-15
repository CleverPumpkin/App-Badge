package ru.cleverpumpkin.badge.kt

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.internal.DefaultDomainObjectSet

/**
 * @author Sergey Chuprin
 */
object ProjectUtilsKt {

    fun getAndroidExtension(project: Project): TestedExtension {
        val appExtension = project.extensions.findByType(AppExtension::class.java)
        if (appExtension != null) {
            return appExtension
        }
        val libraryExtension = project.extensions.findByType(LibraryExtension::class.java)
        if (libraryExtension != null) {
            return libraryExtension
        }
        throw  IllegalStateException(
            "Not an Android application; did you forget to apply application or library plugin?"
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllVariants(extension: TestedExtension): DefaultDomainObjectSet<BaseVariant> {
        if (extension is LibraryExtension) {
            return extension.libraryVariants as DefaultDomainObjectSet<BaseVariant>
        }
        if (extension is AppExtension) {
            return extension.applicationVariants as DefaultDomainObjectSet<BaseVariant>
        }
        throw  IllegalStateException(
            "Cannot retrive variants from extension: " + extension::class.java.simpleName
        )
    }

}