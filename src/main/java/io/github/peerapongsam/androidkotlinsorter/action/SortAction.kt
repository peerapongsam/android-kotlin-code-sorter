package io.github.peerapongsam.androidkotlinsorter.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import io.github.peerapongsam.androidkotlinsorter.sort.Sorter
import org.jetbrains.kotlin.idea.refactoring.memberInfo.KtPsiClassWrapper
import org.jetbrains.kotlin.psi.KtClassOrObject

open class SortAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        getKtClasses(event)?.let {
            startSort(it)
        }
    }

    private fun startSort(classes: List<KtClassOrObject>) {
        for (cls in classes) {
            WriteCommandAction.writeCommandAction(cls.project, cls.containingFile).run<Throwable> {
                Sorter(cls).sort()
            }
        }
    }

    private fun getKtClasses(event: AnActionEvent): List<KtClassOrObject>? {
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return null
        return psiFile.children.filterIsInstance<KtClassOrObject>()
    }
}