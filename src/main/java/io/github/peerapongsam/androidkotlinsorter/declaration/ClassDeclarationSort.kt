package io.github.peerapongsam.androidkotlinsorter.declaration

import com.intellij.ide.util.PropertiesComponent
import io.github.peerapongsam.androidkotlinsorter.config.SortConfigurable
import org.jetbrains.kotlin.psi.KtDeclaration

class ClassDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    private val isOrderInnerClassesByName by lazy {
        PropertiesComponent.getInstance().getBoolean(SortConfigurable.SAVED_ORDER_INNER_CLASSES_BY_NAME, false)
    }

    override fun sort(): List<KtDeclaration> {
        val functions = mutableListOf<KtDeclaration>()
        functions.addAll(sortInner())
        functions.addAll(sortClass())
        functions.addAll(sortInterface())
        functions.addAll(ModifierDeclarationSort(declarations, isOrderInnerClassesByName).sort())
        return functions
    }

    private fun sortInner(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("inner") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderInnerClassesByName).sort()
    }

    private fun sortClass(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("class") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderInnerClassesByName).sort()
    }

    private fun sortInterface(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("interface") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderInnerClassesByName).sort()
    }
}