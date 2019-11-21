package io.github.peerapongsam.androidcodesorter.sortstratregy


import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

abstract class BaseSortStrategy(private val declarations: List<out KtDeclaration>) {

    protected var ordering: List<String>? = null

    fun sort(): List<KtDeclaration> {
        if (ordering == null) {
            return declarations
        }

        val declarationGroup = declarations.groupBy { it.javaClass.name }
        val sortedDeclarations = mutableListOf<KtDeclaration>()
        ordering?.forEach { order ->
            sortedDeclarations.addAll(declarationGroup[order]?.let { list ->
                when (order) {
                    KtProperty::class.java.name -> PropertiesSortStrategy(list.toMutableList()).sort()
                    KtNamedFunction::class.java.name -> FunctionsSortStrategy(list.toMutableList()).sort()
                    KtClass::class.java.name -> ClassSortStrategy(list.toMutableList()).sort()
                    else -> list.sortedBy { it.name }
                }
            } ?: emptyList())
        }

        val map = declarations.map { declaration ->
            "declaration.modifierList = ${declaration.modifierList?.text},\n" +
                    "declaration.name = ${declaration.name},\n" +
                    "declaration.annotations = ${declaration.annotations},\n" +
                    "declaration.text = ${declaration.text},\n" +
                    "declaration.descriptor = ${declaration.descriptor},\n" +
                    "declaration.language = ${declaration.language},\n" +
                    "declaration.visibilityModifierType() = ${declaration.visibilityModifierType()}"
        }

        return sortedDeclarations
    }
}