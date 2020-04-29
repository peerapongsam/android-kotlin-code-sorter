package io.github.peerapongsam.androidkotlinsorter.declaration

import com.intellij.ide.util.PropertiesComponent
import io.github.peerapongsam.androidkotlinsorter.config.SortConfigurable
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration

class ObjectDeclarationSort(private val declarationList: MutableList<KtDeclaration>, private val isCompanion: Boolean) : DeclarationSort {

    private val isOrderInnerClassesByName by lazy {
        PropertiesComponent.getInstance().getBoolean(SortConfigurable.SAVED_ORDER_INNER_CLASSES_BY_NAME, false)
    }

    override fun sort(): List<KtDeclaration> {
        val objectList = declarationList.filter { (it as KtObjectDeclaration).isCompanion() == isCompanion }.toMutableList()
        return ModifierDeclarationSort(objectList, isOrderInnerClassesByName).sort()
    }
}