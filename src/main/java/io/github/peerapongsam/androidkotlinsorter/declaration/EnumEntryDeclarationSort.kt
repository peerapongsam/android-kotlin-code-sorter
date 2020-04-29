package io.github.peerapongsam.androidkotlinsorter.declaration

import org.jetbrains.kotlin.psi.KtDeclaration

class EnumEntryDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {
    override fun sort(): List<KtDeclaration> {
        return declarations
    }
}