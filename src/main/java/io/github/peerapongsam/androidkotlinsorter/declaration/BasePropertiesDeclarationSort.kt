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
open class BasePropertiesDeclarationSort(protected val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    companion object {
        const val TAG = "TAG"
        const val COMPOSITE_DISPOSABLE = "CompositeDisposable"
        const val BACKING_FIELDS = "Backing Fields (val get(), var get(); set(value) {})"
        const val BACKING_PROPERTIES = "Backing Properties (private var _foo; val foo = _foo)"
        const val CONSTANTS = "Constants (const val )"
        const val CUSTOM_DELEGATED_PROPERTIES = "Custom Delegated Properties (by binding)"
        const val INJECT_DELEGATED_PROPERTIES = "Inject Properties (by inject, by viewModel, @Inject lateinit var)"
        const val LATE_INITIALIZED_PROPERTIES = "Late-Initialized (lateinit var)"
        const val MUTABLE_PROPERTIES = "Mutable Properties(var)"
        const val OTHER_PROPERTIES = "Other Properties"
        const val OVERRIDING_PROPERTIES = "Overriding Properties (override val, override var)"
        const val READ_ONLY_PROPERTIES = "Read only Properties(val)"
        const val STANDARD_DELEGATED_PROPERTIES = "Standard Delegated Properties (by lazy)"

        fun getDefaultOrders() = listOf(
                TAG,
                CONSTANTS,
                COMPOSITE_DISPOSABLE,
                OVERRIDING_PROPERTIES,
                READ_ONLY_PROPERTIES,
                MUTABLE_PROPERTIES,
                LATE_INITIALIZED_PROPERTIES,
                INJECT_DELEGATED_PROPERTIES,
                STANDARD_DELEGATED_PROPERTIES,
                CUSTOM_DELEGATED_PROPERTIES,
                BACKING_FIELDS,
                BACKING_PROPERTIES,
                OTHER_PROPERTIES
        )
    }

    protected var propertiesSortOrders: List<String>

    protected val isOrderPropertiesByName by lazy {
        PropertiesComponent.getInstance().getBoolean(SortConfigurable.SAVED_ORDER_PROPERTIES_BY_NAME, false)
    }

    init {
        propertiesSortOrders = getDefaultOrders()
    }

    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        propertiesSortOrders.forEach { order ->
            when (order) {
                TAG -> {
                    sortTagOrJavaCanonicalName()
                }
                CONSTANTS -> {
                    sortConstants()
                }
                COMPOSITE_DISPOSABLE -> {
                    sortCompositeDisposable()
                }
                OVERRIDING_PROPERTIES -> {
                    sortOverridingReadOnlyProperties()
                    sortOverridingMutableProperties()
                }
                READ_ONLY_PROPERTIES -> {
                    properties.addAll(sortReadOnlyProperties())
                }
                MUTABLE_PROPERTIES -> {
                    properties.addAll(sortMutableProperties())
                }
                LATE_INITIALIZED_PROPERTIES -> {
                    properties.addAll(sortLateInitializedProperties())
                }
                INJECT_DELEGATED_PROPERTIES -> {
                    properties.addAll(sortInjectProperties())
                }
                STANDARD_DELEGATED_PROPERTIES -> {
                    properties.addAll(sortStandardDelegatedProperties())
                }
                CUSTOM_DELEGATED_PROPERTIES -> {
                    properties.addAll(sortCustomDelegateProperties())
                }
                BACKING_FIELDS -> {
//                    sortBackingReadOnlyFields()
//                    sortBackingMutableFields()
                }
                BACKING_PROPERTIES -> {
//                    sortBackingProperties()
                }
                else -> {
                    properties.addAll(ModifierDeclarationSort(declarations.toMutableList(), isOrderPropertiesByName).sort())
                }
            }
        }
        return properties
    }

    protected fun sortCompositeDisposable(): Collection<KtDeclaration> {
        val properties = declarations.filter { it.text.contains("CompositeDisposable()") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList(), isOrderPropertiesByName).sort()
    }

    protected fun isBackingField(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !property.text.contains("return _")
                && ((descriptor.isVar && (descriptor.setter as? PropertySetterDescriptorImpl)?.isDefault != true || descriptor.getter?.isDefault != true)
                || (!descriptor.isVar && descriptor.getter?.isDefault != true))
    }

    protected fun isBackingProperties(property: KtDeclaration): Boolean {
        return (property.visibilityModifierType().toString() == "private" && property.name?.startsWith("_") == true)
                || property.text.contains(" = _")
                || (property.text.contains("get()") && property.text.contains("return _"))
    }

    protected fun isConstant(property: KtDeclaration): Boolean {
        return (property.descriptor as PropertyDescriptorImpl).isConst
    }

    protected fun isCustomDelegateProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !descriptor.isVar && descriptor.isDelegated && !property.text.contains(" by lazy ")
    }

    protected fun isInjectProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        val text = property.text
        return text.contains("@Inject ") || (!descriptor.isVar && descriptor.isDelegated)
                && (text.contains("by inject") || text.contains("by viewModel"))
    }

    protected fun isLateInitializedProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return descriptor.isLateInit && !property.text.contains("@Inject")
    }

    protected fun isMutableProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return descriptor.isVar && !descriptor.isLateInit
                && (descriptor.setter as? PropertySetterDescriptorImpl)?.isDefault == true
                && descriptor.getter?.isDefault == true
    }

    protected fun isOverridingProperty(property: KtDeclaration): Boolean {
        return property.text.startsWith("override")
    }

    protected fun isReadOnlyProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !descriptor.isVar && !descriptor.isDelegated && !descriptor.isConst && descriptor.getter?.isDefault == true
    }

    protected fun isStandardDelegatedProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !descriptor.isVar && descriptor.isDelegated && property.text.contains(" by lazy ")
    }

    protected open fun sortBackingMutableFields(): Collection<KtDeclaration> {
        val properties = declarations.filter { isBackingField(it) && !isBackingProperties(it) && (it.descriptor as PropertyDescriptorImpl).isVar }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortBackingProperties(): Collection<KtDeclaration> {
        val properties = declarations.filter { isBackingProperties(it) }
        declarations.removeAll(properties)
        val propertiesGroup = properties.groupBy {
            it.name?.replace("^_".toRegex(), "") ?: ""
        }.toSortedMap()
        val propertiesResult = mutableListOf<KtDeclaration>()
        propertiesGroup.forEach { map ->
            propertiesResult.addAll(map.value.sortedBy { it.name })
        }
        return propertiesResult
    }

    protected open fun sortBackingReadOnlyFields(): List<KtDeclaration> {
        val properties = declarations.filter { isBackingField(it) && !isBackingProperties(it) && !(it.descriptor as PropertyDescriptorImpl).isVar }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortConstants(): List<KtDeclaration> {
        val properties = declarations.filter { isConstant(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortCustomDelegateProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isCustomDelegateProperty(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortInjectProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isInjectProperty(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortLateInitializedProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isLateInitializedProperty(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortMutableProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isMutableProperty(it) && !isBackingField(it) && !isBackingProperties(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortOverridingMutableProperties(): List<KtDeclaration> {
        val properties = declarations.filter {
            isOverridingProperty(it) && (it.descriptor as PropertyDescriptorImpl).isVar
        }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList(), isOrderPropertiesByName).sort()
    }

    protected open fun sortOverridingReadOnlyProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isOverridingProperty(it) && !(it.descriptor as PropertyDescriptorImpl).isVar }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList(), isOrderPropertiesByName).sort()
    }

    protected open fun sortReadOnlyProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isReadOnlyProperty(it) && !isBackingField(it) && !isBackingProperties(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortStandardDelegatedProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isStandardDelegatedProperty(it) }
        declarations.removeAll(properties)
        return properties
    }

    protected open fun sortTagOrJavaCanonicalName(): List<KtDeclaration> {
        val properties = declarations.filter { it.name == "TAG" || it.text.contains("::class.java.canonicalName") }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList(), isOrderPropertiesByName).sort()
    }
}