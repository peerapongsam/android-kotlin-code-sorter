package io.github.peerapongsam.androidkotlinsorter.sortstratregy


import io.github.peerapongsam.androidkotlinsorter.declaration.ClassDeclarationSort
import io.github.peerapongsam.androidkotlinsorter.declaration.FunctionsDeclarationSort
import io.github.peerapongsam.androidkotlinsorter.declaration.PropertiesDeclarationSort
import io.github.peerapongsam.androidkotlinsorter.declaration.SecondaryConstructorDeclarationSort
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.*

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

        val map = sortedDeclarations.map {

            val descriptorImpl = it.descriptor as? PropertyDescriptorImpl
            Pair("${it.name}(\n" +
                    "isVar = ${descriptorImpl?.isVar},\n" +
                    "isDelegated = ${descriptorImpl?.isDelegated},\n" +
                    "isLateInit = ${descriptorImpl?.isLateInit},\n" +
                    "isActual = ${descriptorImpl?.isActual},\n" +
                    "isConst = ${descriptorImpl?.isConst},\n" +
                    "isExpect = ${descriptorImpl?.isExpect},\n" +
                    "isExternal = ${descriptorImpl?.isExternal}\n" +
                    ")", it.descriptor)
        }

        return sortedDeclarations
    }
}