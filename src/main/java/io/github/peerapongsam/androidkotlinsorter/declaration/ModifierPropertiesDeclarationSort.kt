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
        if (isOrderPropertiesByName) {
            properties.addAll(declarations.sortedBy { it.name })
        } else {
            properties.addAll(declarations)
        }
        return properties
    }

    private fun sortPublic(): List<KtDeclaration> {
        val functions = declarations.filter { (it.visibilityModifierType() == null || it.visibilityModifierType().toString() == "public") }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }

    private fun sortProtected(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "protected" }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }

    private fun sortInternal(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "internal" }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }

    private fun sortPrivate(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "private" }
        declarations.removeAll(functions)
        return if (isOrderPropertiesByName) {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort().sortedBy { it.name }
        } else {
            BasePropertiesDeclarationSort(functions.toMutableList()).sort()
        }
    }
}