package io.github.peerapongsam.androidkotlinsorter.declaration

import com.intellij.ide.util.PropertiesComponent
import io.github.peerapongsam.androidkotlinsorter.config.SortConfigurable
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * static
 * val
 * var
 * lateinit var
 * by inject, by viewModel
 * lazy
 */
class TypePropertiesDeclarationSort(declarations: MutableList<KtDeclaration>) : BasePropertiesDeclarationSort(declarations) {

    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        propertiesSortOrders.forEach { order ->
            when (order) {
                TAG -> {
                    properties.addAll(sortTagOrJavaCanonicalName())
                }
                CONSTANTS -> {
                    properties.addAll(sortConstants())
                }
                COMPOSITE_DISPOSABLE -> {
                    properties.addAll(sortCompositeDisposable())
                }
                OVERRIDING_PROPERTIES -> {
                    properties.addAll(sortOverridingReadOnlyProperties())
                    properties.addAll(sortOverridingMutableProperties())
                }
                READ_ONLY_PROPERTIES -> {
                    properties.addAll(sortReadOnlyProperties())
                }
                MUTABLE_PROPERTIES -> {
                    properties.addAll(sortMutableProperties())
                }
                LATE_INITIALIZED_PROPERTIES -> {
                    properties.addAll(sortLateInitializedProperties())
                }
                INJECT_DELEGATED_PROPERTIES -> {
                    properties.addAll(sortInjectProperties())
                }
                STANDARD_DELEGATED_PROPERTIES -> {
                    properties.addAll(sortStandardDelegatedProperties())
                }
                CUSTOM_DELEGATED_PROPERTIES -> {
                    properties.addAll(sortCustomDelegateProperties())
                }
                BACKING_FIELDS -> {
                    properties.addAll(sortBackingReadOnlyFields())
                    properties.addAll(sortBackingMutableFields())
                }
                BACKING_PROPERTIES -> {
                    properties.addAll(sortBackingProperties())
                }
                else -> {
                    properties.addAll(ModifierDeclarationSort(declarations.toMutableList(), isOrderPropertiesByName).sort())
                }
            }
        }
        return properties
    }

    override fun sortReadOnlyProperties(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortReadOnlyProperties().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortMutableProperties(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortMutableProperties().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortLateInitializedProperties(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortLateInitializedProperties().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortInjectProperties(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortInjectProperties().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortStandardDelegatedProperties(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortStandardDelegatedProperties().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortCustomDelegateProperties(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortCustomDelegateProperties().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortBackingReadOnlyFields(): List<KtDeclaration> {
        return ModifierDeclarationSort(super.sortBackingReadOnlyFields().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortBackingMutableFields(): Collection<KtDeclaration> {
        return ModifierDeclarationSort(super.sortBackingMutableFields().toMutableList(), isOrderPropertiesByName).sort()
    }

    override fun sortBackingProperties(): Collection<KtDeclaration> {
        return ModifierDeclarationSort(super.sortBackingProperties().toMutableList(), isOrderPropertiesByName).sort()
    }
}