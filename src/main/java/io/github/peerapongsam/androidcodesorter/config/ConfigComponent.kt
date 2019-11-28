package io.github.peerapongsam.androidcodesorter.config

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.SearchableConfigurable
import io.github.peerapongsam.androidcodesorter.declaration.PropertiesDeclarationSort
import io.github.peerapongsam.androidcodesorter.sortstratregy.CommonSortStrategy
import io.github.peerapongsam.androidcodesorter.ui.KotlinArrangementSettings
import javax.swing.JComponent

class ConfigComponent : SearchableConfigurable {

    companion object {
        const val KEY_SAVE_ORDER_ARRAY_KEY = "save_order_array_key"
        const val KEY_KEEP_VAL_VAR_TOGETHER = "keep_val_var_together"
        const val KEY_PROPERTIES_ORDER = "properties_order"
        const val KEY_KEEP_LATEINIT_VAR_TOGETHER_WITH_VAR = "keep_lateinit_var_together_with_val_var"
        const val KEY_KEEP_LAZY_INITIAL_TOGETHER_WITH_VAL = "keep_lazy_initial_together_with_val_var"
        const val KEY_KEEP_LAZY_INITIALIZE_TOGETHER = "keep_lazy_initialize_together"

        fun Array<String>?.swap(a: Int, b: Int): Array<String> = this
                ?.toMutableList()
                ?.also {
                    it[a] = this[b]
                    it[b] = this[a]
                }?.toTypedArray() ?: emptyArray()
    }

    private lateinit var originData: ConfigData
    private lateinit var currentData: ConfigData

    private lateinit var state: PropertiesComponent
    private val settings: KotlinArrangementSettings by lazy { KotlinArrangementSettings() }

    override fun isModified(): Boolean {
        return originData != currentData
    }

    override fun getId(): String {
        return displayName
    }

    override fun getDisplayName(): String {
        return "Android Kotlin Sorter"
    }

    override fun apply() {
        state.setValue(KEY_KEEP_VAL_VAR_TOGETHER, currentData.keepValVarTogether)
        state.setValue(KEY_KEEP_LATEINIT_VAR_TOGETHER_WITH_VAR, currentData.keepLateinitVarTogetherWithValVar)
        state.setValue(KEY_KEEP_LAZY_INITIAL_TOGETHER_WITH_VAL, currentData.keepLazyInitializeTogetherWithVal)
        state.setValues(KEY_PROPERTIES_ORDER, currentData.propertiesOrders)
    }

    override fun createComponent(): JComponent? {
        state = PropertiesComponent.getInstance()
        loadValues()
        initView()
        return settings.mainPanel
    }


    private fun loadValues() {
        var declarationsOrders = state.getValues(KEY_SAVE_ORDER_ARRAY_KEY)
        if (declarationsOrders == null || declarationsOrders.isEmpty()) {
            declarationsOrders = CommonSortStrategy.getDefaultOrdering().toTypedArray()
        }
        var propertiesOrders = state.getValues(KEY_PROPERTIES_ORDER)
        if (propertiesOrders == null || propertiesOrders.isEmpty()) {
            propertiesOrders = PropertiesDeclarationSort.getDefaultOrders().toTypedArray()
        }
        val configData = ConfigData(
                declarationsOrders = declarationsOrders,
                propertiesOrders = propertiesOrders,
                keepValVarTogether = state.getBoolean(KEY_KEEP_VAL_VAR_TOGETHER),
                keepLateinitVarTogetherWithValVar = state.getBoolean(KEY_KEEP_LATEINIT_VAR_TOGETHER_WITH_VAR),
                keepLazyInitializeTogetherWithVal = state.getBoolean(KEY_KEEP_LAZY_INITIAL_TOGETHER_WITH_VAL))

        originData = configData.copy()
        currentData = configData.copy()

        settings.propertiesList.setListData(propertiesOrders)
        settings.keepValVarTogetherCheckBox.isSelected = configData.keepValVarTogether
        settings.keepLateinitTogetherWithCheckBox.isSelected = configData.keepLateinitVarTogetherWithValVar
        settings.keepLazyInitializeTogetherWithValVarCheckBox.isSelected = configData.keepLazyInitializeTogetherWithVal
    }

    private fun initView() {
        with(settings) {
            keepValVarTogetherCheckBox.addActionListener {
                currentData = currentData.copy(
                        keepValVarTogether = keepValVarTogetherCheckBox.isSelected
                )
            }
            keepLateinitTogetherWithCheckBox.addActionListener {
                currentData = currentData?.copy(
                        keepLateinitVarTogetherWithValVar = keepLateinitTogetherWithCheckBox.isSelected
                )
            }
            keepLazyInitializeTogetherWithValVarCheckBox.addActionListener {
                currentData = currentData?.copy(
                        keepLazyInitializeTogetherWithVal = keepLazyInitializeTogetherWithValVarCheckBox.isSelected
                )
            }

            propertiesListUpButton.addActionListener {
                val selectedIndex = propertiesList.selectedIndex
                if (selectedIndex in 1..(currentData?.propertiesOrders?.size ?: 0)) {
                    swapPropertiesList(selectedIndex, selectedIndex - 1)
                }
            }
            propertiesListDownButton.addActionListener {
                val selectedIndex = propertiesList.selectedIndex
                if (selectedIndex in 0..(currentData?.propertiesOrders?.size ?: 0)) {
                    swapPropertiesList(selectedIndex, selectedIndex + 1)
                }
            }

        }
    }

    private fun swapPropertiesList(selectedIndex: Int, newIndex: Int) {
        val propertiesOrders = currentData?.propertiesOrders?.swap(selectedIndex, newIndex) ?: emptyArray()
        currentData = currentData?.copy(propertiesOrders = propertiesOrders)
        settings.propertiesList.setListData(propertiesOrders)
        settings.propertiesList.selectedIndex = newIndex
    }

}