package io.github.peerapongsam.androidcodesorter.sortstratregy

import org.jetbrains.kotlin.psi.*

class CommonSortStrategy(declarations: List<KtDeclaration>) : BaseSortStrategy(declarations) {

    init {
        ordering = getDefaultOrdering()
    }

    companion object {
        fun getDefaultOrdering(): List<String> = listOf(
                KtObjectDeclaration::class.java.name,
                KtProperty::class.java.name,
                KtClassInitializer::class.java.name,
                KtSecondaryConstructor::class.java.name,
                KtNamedFunction::class.java.name,
                KtClass::class.java.name
        )
    }
}