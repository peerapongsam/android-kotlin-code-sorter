package io.github.peerapongsam.androidcodesorter.sortstratregy

import org.jetbrains.kotlin.psi.KtDeclaration

class ClassSortStrategy(private val declarations: MutableList<KtDeclaration>) : SortStrategy {

    override fun sort(): List<KtDeclaration> {
        val functions = mutableListOf<KtDeclaration>()
        functions.addAll(sortInner())
        functions.addAll(ModifierSortStrategy(declarations).sort())
        return functions
    }

    private fun sortInner(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("inner") }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }
}