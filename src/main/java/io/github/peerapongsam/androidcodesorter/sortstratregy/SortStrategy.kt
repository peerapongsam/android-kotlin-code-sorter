package io.github.peerapongsam.androidcodesorter.sortstratregy

import org.jetbrains.kotlin.psi.KtDeclaration

interface SortStrategy {

    fun sort(): List<KtDeclaration>
}