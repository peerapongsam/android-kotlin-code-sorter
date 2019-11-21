package io.github.peerapongsam.androidcodesorter.sortstratregy

import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * override
 * public
 * internal
 * private
 */
class FunctionsSortStrategy(private val declarations: MutableList<KtDeclaration>) : SortStrategy {

    companion object {
        private const val ON_ATTACH = "onAttach"
        private const val ON_CREATE = "onCreate"
        private const val ON_CREATE_VIEW = "onCreateView"
        private const val ON_VIEW_CREATED = "onViewCreated"
        private const val ON_ACTIVITY_CREATED = "onActivityCreated"
        private const val ON_VIEW_STATE_RESTORED = "onViewStateRestored"
        private const val ON_RESTART = "onRestart"
        private const val ON_START = "onStart"
        private const val ON_RESUME = "onResume"
        private const val ON_PAUSE = "onPause"
        private const val ON_STOP = "onStop"
        private const val ON_DESTROY_VIEW = "onDestroyView"
        private const val ON_DESTROY = "onDestroy"
        private const val ON_DETACH = "onDetach"

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

    override fun sort(): List<KtDeclaration> {
        val functions = mutableListOf<KtDeclaration>()
        functions.addAll(sortLifecycle())
        functions.addAll(sortOverride())
        functions.addAll(ModifierSortStrategy(declarations).sort())
        return functions
    }

    private fun sortLifecycle(): List<KtDeclaration> {
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

    private fun sortOverride(): List<KtDeclaration> {
        val functions = declarations.filter { it.text.startsWith("override") }
        declarations.removeAll(functions)
        return functions.sortedBy { it.name }
    }
}