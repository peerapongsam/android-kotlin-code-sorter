package io.github.peerapongsam.androidcodesorter.declaration

import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * static
 * val
 * var
 * lateinit var
 * by inject, by viewModel
 * lazy
 */
class PropertiesDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {
    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        properties.addAll(sortConst())
        properties.addAll(sortVal())
        properties.addAll(sortVar())
        properties.addAll(sortLateInitVar())
        properties.addAll(sortValByInject())
        properties.addAll(sortValByViewModel())
        properties.addAll(sortValByOther())
        properties.addAll(sortValLazy())
        properties.addAll(declarations.sortedBy { it.name })
        return properties
    }

    private fun sortConst(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("const") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortVal(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && !it.text.contains("by") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortVar(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("var") && !it.text.contains("lateinit") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortLateInitVar(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("lateinit var") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortValByInject(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && it.text.contains("by") && it.text.contains("inject") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortValByViewModel(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && it.text.contains("by") && it.text.contains("viewModel") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortValByOther(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && it.text.contains("by") && !it.text.contains("lazy") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortValLazy(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val") && it.text.contains("by") && it.text.contains("lazy") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }
}