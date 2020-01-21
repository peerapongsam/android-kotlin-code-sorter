package io.github.peerapongsam.androidkotlinsorter.config

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.SearchableConfigurable
import io.github.peerapongsam.androidkotlinsorter.ui.SortConfiguration
import javax.swing.JComponent

class SortConfigurable : SearchableConfigurable {

    companion object {
        const val SAVED_ORDER_PROPERTIES_BY_NAME = "SAVED_ORDER_PROPERTIES_BY_NAME"
        const val SAVED_ORDER_ACTIVITY_LIFECYCLE = "SAVED_ORDER_ACTIVITY_LIFECYCLE"
        const val SAVED_ORDER_FUNCTIONS_BY_NAME = "SAVED_ORDER_FUNCTIONS_BY_NAME"
        const val SAVED_ORDER_INNER_CLASSES_BY_NAME = "SAVED_ORDER_INNER_CLASSES_BY_NAME"
    }

    private var originalConfig: Config = Config()
    private var currentConfig: Config = Config()
    private lateinit var state: PropertiesComponent
    private val ui: SortConfiguration by lazy { SortConfiguration() }

    override fun isModified(): Boolean {
        return originalConfig != currentConfig
    }

    override fun getId(): String {
        return displayName
    }

    override fun getDisplayName(): String {
        return "Kotlin Code Sorter"
    }

    override fun apply() {
        state.setValue(SAVED_ORDER_PROPERTIES_BY_NAME, currentConfig.isOrderPropertiesByName)
        state.setValue(SAVED_ORDER_ACTIVITY_LIFECYCLE, currentConfig.isOrderActivityLifeCycle)
        state.setValue(SAVED_ORDER_FUNCTIONS_BY_NAME, currentConfig.isOrderFunctionsByName)
        state.setValue(SAVED_ORDER_INNER_CLASSES_BY_NAME, currentConfig.isOrderInnerClassesByName)
        originalConfig = currentConfig.copy()
    }

    override fun createComponent(): JComponent? {
        state = PropertiesComponent.getInstance()
        loadValues()
        setupUI()
        return ui.mainPanel
    }

    private fun loadValues() {
        val loadedConfig = Config(
                isOrderPropertiesByName = state.getBoolean(SAVED_ORDER_PROPERTIES_BY_NAME, false),
                isOrderActivityLifeCycle = state.getBoolean(SAVED_ORDER_ACTIVITY_LIFECYCLE, false),
                isOrderFunctionsByName = state.getBoolean(SAVED_ORDER_FUNCTIONS_BY_NAME, false),
                isOrderInnerClassesByName = state.getBoolean(SAVED_ORDER_INNER_CLASSES_BY_NAME, false)
        )
        originalConfig = loadedConfig.copy()
        currentConfig = loadedConfig.copy()
    }

    private fun setupUI() {
        ui.orderPropertiesByNameCheckBox.isSelected = currentConfig.isOrderPropertiesByName
        ui.orderActivityFragmentLifecycleCheckBox.isSelected = currentConfig.isOrderActivityLifeCycle
        ui.orderFunctionsByNameCheckBox.isSelected = currentConfig.isOrderFunctionsByName
        ui.orderInnerClassesByCheckBox.isSelected = currentConfig.isOrderInnerClassesByName

        ui.orderPropertiesByNameCheckBox.addActionListener {
            currentConfig = currentConfig.copy(
                    isOrderPropertiesByName = ui.orderPropertiesByNameCheckBox.isSelected
            )
        }

        ui.orderActivityFragmentLifecycleCheckBox.addActionListener {
            currentConfig = currentConfig.copy(
                    isOrderActivityLifeCycle = ui.orderActivityFragmentLifecycleCheckBox.isSelected
            )
        }

        ui.orderFunctionsByNameCheckBox.addActionListener {
            currentConfig = currentConfig.copy(
                    isOrderFunctionsByName = ui.orderFunctionsByNameCheckBox.isSelected
            )
        }

        ui.orderInnerClassesByCheckBox.addActionListener {
            currentConfig = currentConfig.copy(
                    isOrderInnerClassesByName = ui.orderInnerClassesByCheckBox.isSelected
            )
        }
    }
}