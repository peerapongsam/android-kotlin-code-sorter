package io.github.peerapongsam.androidkotlinsorter.action

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import io.github.peerapongsam.androidkotlinsorter.sort.Sorter
import org.jetbrains.kotlin.idea.refactoring.memberInfo.KtPsiClassWrapper
import org.jetbrains.kotlin.preloading.ProfilingInstrumenterExample.e
import org.jetbrains.kotlin.psi.KtClassOrObject

open class SortAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        getKtClasses(event)?.let {
            startSort(it)
        }

        val notificationGroup = NotificationGroup(
                "Android Kotlin Code Sorter",
                NotificationDisplayType.BALLOON,
                true)
        notificationGroup.createNotification("Sorted",
                "Sorted",
                NotificationType.INFORMATION,
                null)
                .notify(event.project)
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