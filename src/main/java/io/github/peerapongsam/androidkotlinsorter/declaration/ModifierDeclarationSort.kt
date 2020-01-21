package io.github.peerapongsam.androidkotlinsorter.declaration

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

class ModifierDeclarationSort(private val declarations: MutableList<KtDeclaration>, private val orderByName: Boolean) : DeclarationSort {
    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        properties.addAll(sortPublic())
        properties.addAll(sortProtected())
        properties.addAll(sortInternal())
        properties.addAll(sortPrivate())
        if (orderByName) {
            properties.addAll(declarations.sortedBy { it.name })
        } else {
            properties.addAll(declarations)
        }
        return properties
    }

    private fun sortPublic(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType() == null || it.visibilityModifierType().toString() == "public" }
        declarations.removeAll(functions)
        return if (orderByName) {
            functions.sortedBy { it.name }
        } else {
            functions
        }
    }

    private fun sortProtected(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "protected" }
        declarations.removeAll(functions)
        return if (orderByName) {
            functions.sortedBy { it.name }
        } else {
            functions
        }
    }

    private fun sortInternal(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "internal" }
        declarations.removeAll(functions)
        return if (orderByName) {
            functions.sortedBy { it.name }
        } else {
            functions
        }
    }

    private fun sortPrivate(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "private" }
        declarations.removeAll(functions)
        return if (orderByName) {
            functions.sortedBy { it.name }
        } else {
            functions
        }
    }
}