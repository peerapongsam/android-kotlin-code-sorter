package io.github.peerapongsam.androidcodesorter.declaration

import com.intellij.ide.util.PropertiesComponent
import io.github.peerapongsam.androidcodesorter.config.ConfigComponent
import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * static
 * val
 * var
 * lateinit var
 * by inject, by viewModel
 * lazy
 */
class PropertiesDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    companion object {
        const val PROPERTY_CONST = "const"
        const val PROPERTY_VAL = "val"
        const val PROPERTY_VAR = "var"
        const val PROPERTY_LATEINIT = "lateinit var"
        const val PROPERTY_BY_INJECT = "val by inject"
        const val PROPERTY_BY_VIEWMODEL = "val by viewModel"
        const val PROPERTY_BY_OTHER = "val by ..."
        const val PROPERTY_BY_LAZY = "val by lazy"

        fun getDefaultOrders() = listOf(
                PROPERTY_CONST,
                PROPERTY_VAL,
                PROPERTY_VAR,
                PROPERTY_LATEINIT,
                PROPERTY_BY_INJECT,
                PROPERTY_BY_VIEWMODEL,
                PROPERTY_BY_OTHER,
                PROPERTY_BY_LAZY
        )
    }

    private var ordering: List<String>
    private var keepValVarTogether = false
    private var keepLateinitVarTogetherWithVal = false
    private var keepLazyInitializeTogetherWithVal = false
    private var keepLazyInitializeTogether = false

    init {
        val instance = PropertiesComponent.getInstance()
        ordering = instance.getValues(ConfigComponent.KEY_PROPERTIES_ORDER)?.toList() ?: getDefaultOrders()
        keepValVarTogether = instance.getBoolean(ConfigComponent.KEY_KEEP_VAL_VAR_TOGETHER)
        keepLateinitVarTogetherWithVal = instance.getBoolean(ConfigComponent.KEY_KEEP_LATEINIT_VAR_TOGETHER_WITH_VAR)
        keepLazyInitializeTogetherWithVal = instance.getBoolean(ConfigComponent.KEY_KEEP_LAZY_INITIAL_TOGETHER_WITH_VAL)
        keepLazyInitializeTogether = instance.getBoolean(ConfigComponent.KEY_KEEP_LAZY_INITIALIZE_TOGETHER)
    }

    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        ordering.forEach { order ->
            when (order) {
                PROPERTY_CONST -> properties.addAll(sortConst())
                PROPERTY_VAL -> properties.addAll(sortVal())
                PROPERTY_VAR -> properties.addAll(sortVar(true))
                PROPERTY_LATEINIT -> properties.addAll(sortLateInitVar(true))
                PROPERTY_BY_INJECT -> properties.addAll(sortValByInject(true))
                PROPERTY_BY_VIEWMODEL -> properties.addAll(sortValByViewModel(true))
                PROPERTY_BY_OTHER -> properties.addAll(sortValByOther(true))
                PROPERTY_BY_LAZY -> properties.addAll(sortValLazy(true))
                else -> properties.addAll(declarations.sortedBy { it.name })
            }
        }
        return properties
    }

    private fun sortConst(): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("const ") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortVal(): List<KtDeclaration> {
        val tempVal = mutableListOf<KtDeclaration>()
        val properties = declarations.filter { isVal(it) }
        declarations.removeAll(properties)

        tempVal.addAll(properties)
        if (keepValVarTogether) {
            tempVal.addAll(sortVar(false))
        }
        if (keepLateinitVarTogetherWithVal) {
            tempVal.addAll(sortLateInitVar(false))
        }
        if (keepLazyInitializeTogetherWithVal) {
            tempVal.addAll(sortValByInject(false))
            tempVal.addAll(sortValByViewModel(false))
            tempVal.addAll(sortValByOther(false))
            tempVal.addAll(sortValLazy(false))
        }
        return ModifierDeclarationSort(tempVal).sort()
    }

    private fun isVal(it: KtDeclaration) = it.text.contains("val ") && !it.text.contains("by ") && !it.text.contains("const ")

    private fun sortVar(sort: Boolean): List<KtDeclaration> {
        val properties = declarations.filter { isVar(it) }
        declarations.removeAll(properties)
        return if (sort) {
            ModifierDeclarationSort(properties.toMutableList()).sort()
        } else {
            properties
        }
    }

    private fun isVar(it: KtDeclaration) = it.text.contains("var ") && !it.text.contains("lateinit ")

    private fun sortLateInitVar(sort: Boolean): List<KtDeclaration> {
        val properties = declarations.filter { isLateinitVar(it) }
        declarations.removeAll(properties)
        return if (sort) {
            ModifierDeclarationSort(properties.toMutableList()).sort()
        } else {
            properties
        }
    }

    private fun isLateinitVar(it: KtDeclaration) = it.text.contains("var ") && it.text.contains("lateinit ")

    private fun sortValByInject(sort: Boolean): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val ") && it.text.contains("by ") && it.text.contains(" inject") }
        declarations.removeAll(properties)
        return if (sort) {
            ModifierDeclarationSort(properties.toMutableList()).sort()
        } else {
            properties
        }
    }

    private fun sortValByViewModel(sort: Boolean): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val ") && it.text.contains("by ") && it.text.contains(" viewModel") }
        declarations.removeAll(properties)
        return if (sort) {
            ModifierDeclarationSort(properties.toMutableList()).sort()
        } else {
            properties
        }
    }

    private fun sortValByOther(sort: Boolean): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val ") && it.text.contains("by ") && !it.text.contains(" lazy") }
        declarations.removeAll(properties)
        return if (sort) {
            ModifierDeclarationSort(properties.toMutableList()).sort()
        } else {
            properties
        }
    }

    private fun sortValLazy(sort: Boolean): List<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("val ") && it.text.contains("by ") && it.text.contains(" lazy") }
        declarations.removeAll(properties)
        return if (sort) {
            ModifierDeclarationSort(properties.toMutableList()).sort()
        } else {
            properties
        }
    }
}