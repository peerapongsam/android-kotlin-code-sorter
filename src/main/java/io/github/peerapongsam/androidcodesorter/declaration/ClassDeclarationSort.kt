package io.github.peerapongsam.androidcodesorter.declaration

import org.jetbrains.kotlin.psi.KtDeclaration

class ClassDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    override fun sort(): List<KtDeclaration> {
        val functions = mutableListOf<KtDeclaration>()
        functions.addAll(sortInner())
        functions.addAll(sortClass())
        functions.addAll(sortInterface())
        functions.addAll(ModifierDeclarationSort(declarations).sort())
        return functions
    }

    private fun sortInner(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("inner") }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }

    private fun sortClass(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("class") }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }


    private fun sortInterface(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("interface") }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }

}