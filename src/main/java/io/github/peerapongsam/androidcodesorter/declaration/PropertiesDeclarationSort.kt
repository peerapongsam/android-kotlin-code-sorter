package io.github.peerapongsam.androidcodesorter.declaration

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

    companion object {

        private const val BACKING_FIELDS = "Backing Fields (val get(), var get(); set(value) {})"
        private const val BACKING_PROPERTIES = "Backing Properties (private var _foo; val foo = _foo)"
        private const val CONSTANTS = "Constants (const val )"
        private const val CUSTOM_DELEGATED_PROPERTIES = "Custom Delegated Properties (by binding)"
        private const val INJECT_DELEGATED_PROPERTIES = "Inject Properties (by inject, by viewModel, @Inject lateinit var)"
        private const val LATE_INITIALIZED_PROPERTIES = "Late-Initialized (lateinit var)"
        private const val MUTABLE_PROPERTIES = "Mutable Properties(var)"
        private const val OTHER_PROPERTIES = "Other Properties"
        private const val OVERRIDING_PROPERTIES = "Overriding Properties (override val, override var)"
        private const val READ_ONLY_PROPERTIES = "Read only Properties(val)"
        private const val STANDARD_DELEGATED_PROPERTIES = "Standard Delegated Properties (by lazy)"

        fun getDefaultOrders() = listOf(
                CONSTANTS,
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

    private var propertiesSortOrders: List<String>

    init {
        propertiesSortOrders = getDefaultOrders()
    }

    override fun sort(): List<KtDeclaration> {
        val properties = mutableListOf<KtDeclaration>()
        propertiesSortOrders.forEach { order ->
            when (order) {
                CONSTANTS -> properties.addAll(sortConstants())
                OVERRIDING_PROPERTIES -> {
                    properties.addAll(sortOverridingReadOnlyProperties())
                    properties.addAll(sortOverridingMutableProperties())
                }
                READ_ONLY_PROPERTIES -> properties.addAll(sortReadOnlyProperties())
                MUTABLE_PROPERTIES -> properties.addAll(sortMutableProperties())
                LATE_INITIALIZED_PROPERTIES -> properties.addAll(sortLateInitializedProperties())
                INJECT_DELEGATED_PROPERTIES -> properties.addAll(sortInjectProperties())
                STANDARD_DELEGATED_PROPERTIES -> properties.addAll(sortStandardDelegatedProperties())
                CUSTOM_DELEGATED_PROPERTIES -> properties.addAll(sortCustomDelegateProperties())
                BACKING_FIELDS -> {
                    properties.addAll(sortBackingReadOnlyFields())
                    properties.addAll(sortBackingMutableFields())
                }
                BACKING_PROPERTIES -> properties.addAll(sortBackingProperties())
                else -> properties.addAll(ModifierDeclarationSort(declarations.toMutableList()).sort())
            }
        }

        val tmap = properties.map { it.text }
        return properties
    }

    private fun isBackingField(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !property.text.contains("return _") && (descriptor.setter as? PropertySetterDescriptorImpl)?.isDefault != true || descriptor.getter?.isDefault != true
    }

    private fun isBackingProperties(property: KtDeclaration): Boolean {
        return (property.visibilityModifierType().toString() == "private" && property.name?.startsWith("_") == true)
                || property.text.contains(" = _")
                || (property.text.contains("get()") && property.text.contains("return _"))
    }

    private fun isConstant(property: KtDeclaration): Boolean {
        return (property.descriptor as PropertyDescriptorImpl).isConst
    }

    private fun isCustomDelegateProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !descriptor.isVar && descriptor.isDelegated && !property.text.contains(" by lazy ")
    }

    private fun isInjectProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        val text = property.text
        return text.contains("@Inject ") || (!descriptor.isVar && descriptor.isDelegated)
                && (text.contains("by inject") || text.contains("by viewModel"))
    }

    private fun isLateInitializedProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return descriptor.isLateInit && !property.text.contains("@Inject")
    }

    private fun isMutableProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return descriptor.isVar && !descriptor.isLateInit
                && (descriptor.setter as? PropertySetterDescriptorImpl)?.isDefault == true
                && descriptor.getter?.isDefault == true
    }

    private fun isOverridingProperty(property: KtDeclaration): Boolean {
        return property.text.startsWith("override")
    }

    private fun isReadOnlyProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !descriptor.isVar && !descriptor.isDelegated && !descriptor.isConst && descriptor.getter?.isDefault == true
    }

    private fun isStandardDelegatedProperty(property: KtDeclaration): Boolean {
        val descriptor = property.descriptor as PropertyDescriptorImpl
        return !descriptor.isVar && descriptor.isDelegated && property.text.contains(" by lazy ")
    }

    private fun sortBackingMutableFields(): Collection<KtDeclaration> {
        val properties = declarations.filter { isBackingField(it) && !isBackingProperties(it) && (it.descriptor as PropertyDescriptorImpl).isVar }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortBackingProperties(): Collection<KtDeclaration> {
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

    private fun sortBackingReadOnlyFields(): List<KtDeclaration> {
        val properties = declarations.filter { isBackingField(it) && !isBackingProperties(it) && !(it.descriptor as PropertyDescriptorImpl).isVar }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortConstants(): List<KtDeclaration> {
        val properties = declarations.filter { isConstant(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortCustomDelegateProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isCustomDelegateProperty(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortInjectProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isInjectProperty(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortLateInitializedProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isLateInitializedProperty(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortMutableProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isMutableProperty(it) && !isBackingField(it) && !isBackingProperties(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortOverridingMutableProperties(): List<KtDeclaration> {
        val properties = declarations.filter {
            isOverridingProperty(it) && (it.descriptor as PropertyDescriptorImpl).isVar
        }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortOverridingReadOnlyProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isOverridingProperty(it) && !(it.descriptor as PropertyDescriptorImpl).isVar }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortReadOnlyProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isReadOnlyProperty(it) && !isBackingField(it) && !isBackingProperties(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }

    private fun sortStandardDelegatedProperties(): List<KtDeclaration> {
        val properties = declarations.filter { isStandardDelegatedProperty(it) }
        declarations.removeAll(properties)
        return ModifierDeclarationSort(properties.toMutableList()).sort()
    }
}