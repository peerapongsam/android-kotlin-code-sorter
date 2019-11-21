package io.github.peerapongsam.androidcodesorter.declaration

import org.jetbrains.kotlin.psi.KtDeclaration

interface DeclarationSort {
    fun sort(): List<KtDeclaration>
}