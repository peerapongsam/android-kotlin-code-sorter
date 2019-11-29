package io.github.peerapongsam.androidkotlinsorter.declaration

import org.jetbrains.kotlin.idea.search.usagesSearch.constructor
import org.jetbrains.kotlin.psi.KtDeclaration

class SecondaryConstructorDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    override fun sort(): List<KtDeclaration> {
        return declarations.sortedBy { it.constructor.toString().length }
    }
}