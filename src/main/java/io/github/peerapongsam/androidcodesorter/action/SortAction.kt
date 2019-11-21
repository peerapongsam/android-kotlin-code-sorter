package io.github.peerapongsam.androidcodesorter.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import io.github.peerapongsam.androidcodesorter.sort.Sorter
import org.jetbrains.kotlin.psi.KtClassOrObject

class SortAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val psiClasses = getPsiClassFromContext(event)
        if (psiClasses != null) {
            startSort(psiClasses)
        }
    }

    private fun startSort(psiClasses: List<KtClassOrObject>) {
        for (clazz in psiClasses) {
            WriteCommandAction.writeCommandAction(clazz.project as Project, clazz.containingFile as PsiFile).run<Throwable> {
                Sorter(clazz).sort()
            }
        }
    }

    protected fun getPsiClassFromContext(event: AnActionEvent): List<KtClassOrObject>? {
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return null
        val children = psiFile.children
        val result = mutableListOf<KtClassOrObject>()
        for (child in children) {
            if (child is KtClassOrObject) {
                result.add(child)
            }
        }
        return result
    }
}