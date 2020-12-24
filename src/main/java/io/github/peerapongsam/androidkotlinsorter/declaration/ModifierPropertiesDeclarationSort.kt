package io.github.peerapongsam.androidkotlinsorter.declaration

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
class ModifierPropertiesDeclarationSort(declarations: MutableList<KtDeclaration>) : BasePropertiesDeclarationSort(declarations) {

    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        val backingProperties = mutableListOf<KtDeclaration>()
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
                BACKING_FIELDS -> {
                    backingProperties.addAll(sortBackingReadOnlyFields())
                    backingProperties.addAll(sortBackingMutableFields())
                }
                BACKING_PROPERTIES -> {
                    backingProperties.addAll(sortBackingProperties())
                }
                else -> {
                    properties.addAll(sortModifier())
                }
            }
        }
        properties.addAll(backingProperties)
        return properties
    }

    private fun sortModifier(): MutableList<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        properties.addAll(sortPublic())
        properties.addAll(sortProtected())
        properties.addAll(sortInternal())
        properties.addAll(sortPrivate())
        properties.addAll(sortElse())
        return properties
    }

    private fun sortElse(): List<KtDeclaration> {
        val filter = declarations.filter { !isBackingProperties(it) }
        declarations.removeAll(filter)
        return if (isOrderPropertiesByName) {
            filter.sortedBy { it.name }
        } else {
            filter
        }
    }

    private fun sortPublic(): List<KtDeclaration> {
        val functions = declarations.filter { (it.visibilityModifierType() == null || it.visibilityModifierType().toString() == "public") && !isBackingProperties(it) }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }

    private fun sortProtected(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "protected" && !isBackingProperties(it) }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }

    private fun sortInternal(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "internal" && !isBackingProperties(it) }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }

    private fun sortPrivate(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "private" && !isBackingProperties(it) }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }
}