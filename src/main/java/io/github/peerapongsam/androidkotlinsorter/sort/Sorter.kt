package io.github.peerapongsam.androidkotlinsorter.sort

import io.github.peerapongsam.androidkotlinsorter.sortstratregy.CommonSortStrategy
import org.jetbrains.kotlin.psi.KtClassOrObject

class Sorter(private val psiClass: KtClassOrObject) {

    fun sort() {
        realSort(psiClass)
    }

    private fun realSort(classOrObject: KtClassOrObject) {
        val declarations = classOrObject.declarations
        val before = declarations.hashCode()
        val sort = CommonSortStrategy(declarations).sort()
        val after = sort.hashCode()
        sort.forEach {
            if (it is KtClassOrObject) {
                realSort(it)
            }
        }
        if (before != after) {
            sort.forEach {
                classOrObject.addDeclaration(it)
                classOrObject
            }
            declarations.forEach {
                it.delete()
            }
        }
    }
}