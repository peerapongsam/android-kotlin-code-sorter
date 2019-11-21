package io.github.peerapongsam.androidcodesorter.sortstratregy


import io.github.peerapongsam.androidcodesorter.declaration.ClassDeclarationSort
import io.github.peerapongsam.androidcodesorter.declaration.FunctionsDeclarationSort
import io.github.peerapongsam.androidcodesorter.declaration.PropertiesDeclarationSort
import io.github.peerapongsam.androidcodesorter.declaration.SecondaryConstructorDeclarationSort
import org.jetbrains.kotlin.idea.search.usagesSearch.constructor
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.parameterIndex
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

abstract class BaseSortStrategy(private val declarations: List<KtDeclaration>) {

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
                    KtProperty::class.java.name -> PropertiesDeclarationSort(list.toMutableList()).sort()
                    KtSecondaryConstructor::class.java.name -> SecondaryConstructorDeclarationSort(list.toMutableList()).sort()
                    KtNamedFunction::class.java.name -> FunctionsDeclarationSort(list.toMutableList()).sort()
                    KtClass::class.java.name -> ClassDeclarationSort(list.toMutableList()).sort()
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
                    "declaration.constructor = ${declaration.constructor},\n" +
                    "declaration.containingClassOrObject = ${declaration.containingClassOrObject},\n" +
                    "declaration.visibilityModifierType() = ${declaration.visibilityModifierType()},\n" +
                    "declaration.parameterIndex() = ${declaration.parameterIndex()}"
        }

        return sortedDeclarations
    }
}