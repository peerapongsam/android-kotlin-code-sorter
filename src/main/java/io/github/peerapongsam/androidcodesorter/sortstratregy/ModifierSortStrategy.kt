package io.github.peerapongsam.androidcodesorter.sortstratregy

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

class ModifierSortStrategy(private val declarations: MutableList<KtDeclaration>) : SortStrategy {
    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        properties.addAll(sortPublic())
        properties.addAll(sortProtected())
        properties.addAll(sortInternal())
        properties.addAll(sortPrivate())
        properties.addAll(declarations.sortedBy { it.name })
        return properties
    }

    private fun sortPublic(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType() == null || it.visibilityModifierType().toString() == "public" }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }

    private fun sortProtected(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "protected" }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }

    private fun sortInternal(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "internal" }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }

    private fun sortPrivate(): List<KtDeclaration> {
        val functions = declarations.filter { it.visibilityModifierType().toString() == "private" }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }
}