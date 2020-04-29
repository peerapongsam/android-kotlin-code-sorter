package io.github.peerapongsam.androidkotlinsorter.declaration

import com.intellij.ide.util.PropertiesComponent
import io.github.peerapongsam.androidkotlinsorter.config.SortConfigurable
import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * override
 * public
 * internal
 * private
 */
class FunctionsDeclarationSort(private val declarations: MutableList<KtDeclaration>) : DeclarationSort {

    companion object {
        private const val ON_ACTIVITY_CREATED = "onActivityCreated"
        private const val ON_ATTACH = "onAttach"
        private const val ON_CREATE = "onCreate"
        private const val ON_CREATE_VIEW = "onCreateView"
        private const val ON_DESTROY = "onDestroy"
        private const val ON_DESTROY_VIEW = "onDestroyView"
        private const val ON_DETACH = "onDetach"
        private const val ON_PAUSE = "onPause"
        private const val ON_RESTART = "onRestart"
        private const val ON_RESUME = "onResume"
        private const val ON_START = "onStart"
        private const val ON_STOP = "onStop"
        private const val ON_VIEW_CREATED = "onViewCreated"
        private const val ON_VIEW_STATE_RESTORED = "onViewStateRestored"

        private val LIFECYCLE_ORDERING = listOf(
                ON_ATTACH,
                ON_CREATE,
                ON_CREATE_VIEW,
                ON_VIEW_CREATED,
                ON_ACTIVITY_CREATED,
                ON_VIEW_STATE_RESTORED,
                ON_RESTART,
                ON_START,
                ON_RESUME,
                ON_PAUSE,
                ON_STOP,
                ON_DESTROY_VIEW,
                ON_DESTROY,
                ON_DETACH
        )
    }

    private val isOrderActivityLifeCycle by lazy {
        PropertiesComponent.getInstance().getBoolean(SortConfigurable.SAVED_ORDER_ACTIVITY_LIFECYCLE, false)
    }

    private val isOrderFunctionsByName by lazy {
        PropertiesComponent.getInstance().getBoolean(SortConfigurable.SAVED_ORDER_FUNCTIONS_BY_NAME, false)
    }

    override fun sort(): List<KtDeclaration> {
        val functions = mutableListOf<KtDeclaration>()
        val sortEventBusSubscribeFunctions = sortEventBusSubscribeFunctions()
        if (isOrderActivityLifeCycle) {
            functions.addAll(sortLifecycleFunctions())
        }
        functions.addAll(sortOverrideFunctions())
        functions.addAll(sortLiveDataFunctions())
        functions.addAll(sortUnitTestBeforeFunctions())
        functions.addAll(sortUnitTestAfterFunctions())
        functions.addAll(sortUnitTestTestFunctions())
        functions.addAll(ModifierDeclarationSort(declarations.toMutableList(), isOrderFunctionsByName).sort())
        functions.addAll(sortEventBusSubscribeFunctions)
        return functions
    }

    private fun sortLiveDataFunctions(): Collection<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("fun.+on.+\\(\\):.+LiveData.+".toRegex()) }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderFunctionsByName).sort()
    }

    private fun sortLifecycleFunctions(): List<KtDeclaration> {
        val functions = declarations.filter { it.name in LIFECYCLE_ORDERING }
        declarations.removeAll(functions)
        val lifecycleFunctions = mutableListOf<KtDeclaration>()
        LIFECYCLE_ORDERING.forEach { lifecycle ->
            functions.filter { it.name == lifecycle }.let {
                lifecycleFunctions.addAll(it)
            }
        }
        return lifecycleFunctions
    }

    private fun sortOverrideFunctions(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.startsWith("override") }
        declarations.removeAll(functions)
        return if (isOrderFunctionsByName) {
            functions.sortedBy { it.name }
        } else {
            functions
        }
    }

    private fun sortUnitTestAfterFunctions(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("@After") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderFunctionsByName).sort()
    }

    private fun sortUnitTestBeforeFunctions(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("@Before") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderFunctionsByName).sort()
    }

    private fun sortUnitTestTestFunctions(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("@Test") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderFunctionsByName).sort()
    }

    private fun sortEventBusSubscribeFunctions(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.contains("@Subscribe") }
        declarations.removeAll(functions)
        return ModifierDeclarationSort(functions.toMutableList(), isOrderFunctionsByName).sort()
    }
}