package io.github.peerapongsam.androidkotlinsorter.declaration

import org.jetbrains.kotlin.psi.KtDeclaration

interface DeclarationSort {
    fun sort(): List<KtDeclaration>
}