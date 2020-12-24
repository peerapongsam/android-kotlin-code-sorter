package io.github.peerapongsam.androidkotlinsorter.sortstratregy


import io.github.peerapongsam.androidkotlinsorter.declaration.*
import io.github.peerapongsam.androidkotlinsorter.model.SortOrder
import io.github.peerapongsam.androidkotlinsorter.util.Utils
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.EnumEntrySyntheticClassDescriptor
import org.jetbrains.kotlin.descriptors.isFinalOrEnum
import org.jetbrains.kotlin.idea.core.isInheritable
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.*

abstract class BaseSortStrategy(private val declarations: List<KtDeclaration>) {

    protected var sortOrder: List<SortOrder>? = null

    fun sort(): List<KtDeclaration> {
        if (sortOrder == null) {
            return declarations
        }

        val declarationGroup = declarations.groupBy { it.javaClass.name }
        val sortedDeclarations = mutableListOf<KtDeclaration>()
        sortOrder?.forEach { order ->
            sortedDeclarations.addAll(declarationGroup[order.className]?.let { list ->
                when (order.className) {
                    KtObjectDeclaration::class.java.name -> {
                        ObjectDeclarationSort(list.toMutableList(), order.isCompanion).sort()
                    }
                    KtEnumEntry::class.java.name -> {
                        EnumEntryDeclarationSort(list.toMutableList()).sort()
                    }
                    KtProperty::class.java.name -> {
                        PropertiesDeclarationSort(list.toMutableList()).sort()
                    }
                    KtSecondaryConstructor::class.java.name -> {
                        SecondaryConstructorDeclarationSort(list.toMutableList()).sort()
                    }
                    KtNamedFunction::class.java.name -> {
                        FunctionsDeclarationSort(list.toMutableList()).sort()
                    }
                    KtClass::class.java.name -> {
                        ClassDeclarationSort(list.toMutableList()).sort()
                    }
                    else -> {
                        list.sortedBy { it.name }
                    }
                }
            } ?: emptyList())
        }

//
//        val map = sortedDeclarations.map {
//            when (val descriptor = it.descriptor) {
//                is FunctionDescriptor -> {
//                    Pair("${it.name}(\n" +
//                            "text = ${it.text},\n" +
//                            "isInline = ${descriptor.isInline},\n" +
//                            "isInfix = ${descriptor.isInfix},\n" +
//                            "isOperator = ${descriptor.isOperator},\n" +
//                            "isSuspend = ${descriptor.isSuspend},\n" +
//                            "isTailrec = ${descriptor.isTailrec},\n" +
//                            "isExpect = ${descriptor.isExpect},\n" +
//                            "isExternal = ${descriptor.isExternal}\n" +
//                            "original.overriddenDescriptors = ${descriptor.original.overriddenDescriptors.firstOrNull()?.containingDeclaration?.name}\n" +
//                            "getDefinedClass = ${Utils.getDefinedClass(descriptor)}\n" +
//                            ")", descriptor)
//                }
//                is PropertyDescriptor -> {
//                    Pair("${it.name}", descriptor)
//                    Pair("${it.name}(\n" +
//                            "text = ${it.text},\n" +
//                            "isVar = ${descriptor.isVar},\n" +
//                            "getter = ${descriptor.getter},\n" +
//                            "setter = ${descriptor.setter},\n" +
//                            "isDelegated = ${descriptor.isDelegated},\n" +
//                            "isLateInit = ${descriptor.isLateInit},\n" +
//                            "isActual = ${descriptor.isActual},\n" +
//                            "isConst = ${descriptor.isConst},\n" +
//                            "isExpect = ${descriptor.isExpect},\n" +
//                            "isExternal = ${descriptor.isExternal}\n" +
//                            "backingField = ${descriptor.backingField}\n" +
//                            "delegateField = ${descriptor.delegateField}\n" +
//                            ")", it.descriptor)
//                }
//                is ClassDescriptor -> {
//                    Pair("${it.name}", descriptor)
//                    Pair("${it.name}(\n" +
//                            "text = ${it.text},\n" +
//                            "isVar = ${descriptor.isCompanionObject},\n" +
//                            "isData = ${descriptor.isData},\n" +
//                            "isInner = ${descriptor.isInner},\n" +
//                            "isInline = ${descriptor.isInline},\n" +
//                            "isFinalOrEnum = ${descriptor.isFinalOrEnum},\n" +
//                            "isExpect = ${descriptor.isExpect},\n" +
//                            "isExternal = ${descriptor.isExternal}\n" +
//                            ")", it.descriptor)
//                }
//                is EnumEntrySyntheticClassDescriptor -> {
//                    Pair("${it.name}", descriptor)
//                }
//                else -> {
//                    Pair("${it.name}", descriptor)
//                }
//            }
//        }.also {
//            println(it.joinToString("\n"))
//        }

        sortedDeclarations.map {
            Triple(it.name, it.text, it.descriptor)
        }.also {
            println(it.joinToString("\n"))
        }

        return sortedDeclarations
    }
}