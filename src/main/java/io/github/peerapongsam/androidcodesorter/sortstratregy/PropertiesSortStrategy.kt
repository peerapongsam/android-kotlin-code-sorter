package io.github.peerapongsam.androidcodesorter.sortstratregy

import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * static
 * val
 * var
 * lateinit var
 * by inject, by viewModel
 * lazy
 */
class PropertiesSortStrategy(private val declarations: MutableList<KtDeclaration>) : SortStrategy {
    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        properties.addAll(sortConst())
        properties.addAll(sortVal())
        properties.addAll(sortVar())
        properties.addAll(sortLateInitVar())
        properties.addAll(sortValBy())
        properties.addAll(sortValLazy())
        properties.addAll(declarations.sortedBy { it.name })
        return properties
    }

    private fun sortConst(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("const") }
        declarations.removeAll(properties)
        return ModifierSortStrategy(properties.toMutableList()).sort()
    }

    private fun sortVal(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && !it.text.contains("by") }
        declarations.removeAll(properties)
        return ModifierSortStrategy(properties.toMutableList()).sort()
    }

    private fun sortVar(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("var") && !it.text.contains("lateinit") }
        declarations.removeAll(properties)
        return ModifierSortStrategy(properties.toMutableList()).sort()
    }

    private fun sortLateInitVar(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("lateinit var") }
        declarations.removeAll(properties)
        return ModifierSortStrategy(properties.toMutableList()).sort()
    }

    private fun sortValBy(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && it.text.contains("by") && !it.text.contains("lazy") }
        declarations.removeAll(properties)
        return ModifierSortStrategy(properties.toMutableList()).sort()
    }

    private fun sortValLazy(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && it.text.contains("by") && it.text.contains("lazy") }
        declarations.removeAll(properties)
        return ModifierSortStrategy(properties.toMutableList()).sort()
    }
}