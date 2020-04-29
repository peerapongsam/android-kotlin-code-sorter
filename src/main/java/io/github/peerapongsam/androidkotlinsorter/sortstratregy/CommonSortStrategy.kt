package io.github.peerapongsam.androidkotlinsorter.sortstratregy

import io.github.peerapongsam.androidkotlinsorter.model.SortOrder
import org.jetbrains.kotlin.psi.*

class CommonSortStrategy(declarations: List<KtDeclaration>) : BaseSortStrategy(declarations) {

    init {
        sortOrder = getDefaultOrdering()
    }

    companion object {
        fun getDefaultOrdering(): List<SortOrder> = listOf(
                SortOrder(KtEnumEntry::class.java.name),
                SortOrder(KtObjectDeclaration::class.java.name, true),
                SortOrder(KtProperty::class.java.name),
                SortOrder(KtSecondaryConstructor::class.java.name),
                SortOrder(KtClassInitializer::class.java.name),
                SortOrder(KtNamedFunction::class.java.name),
                SortOrder(KtClass::class.java.name),
                SortOrder(KtObjectDeclaration::class.java.name, false)
        )
    }
}