package io.github.peerapongsam.androidkotlinsorter.declaration

import com.intellij.ide.util.PropertiesComponent
import io.github.peerapongsam.androidkotlinsorter.config.SortConfigurable
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * static
 * val
 * var
 * lateinit var
 * by inject, by viewModel
 * lazy
 */
class PropertiesDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    private val isOrderPropertiesVisibility by lazy {
        PropertiesComponent.getInstance().getBoolean(SortConfigurable.SAVED_ORDER_PROPERTIES_BY_NAME, false)
    }

    override fun sort(): List<KtDeclaration> {
        //return TypePropertiesDeclarationSort(declarations).sort()
        return ModifierPropertiesDeclarationSort(declarations).sort()
    }
}