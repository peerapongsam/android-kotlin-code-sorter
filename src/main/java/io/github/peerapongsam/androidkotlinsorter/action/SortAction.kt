package io.github.peerapongsam.androidkotlinsorter.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import io.github.peerapongsam.androidkotlinsorter.sort.Sorter
import org.jetbrains.kotlin.psi.KtClassOrObject

open class SortAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        getKtClasses(event)?.let {
            startSort(it)
        }
//        val notificationGroup = NotificationGroup(
//                "Android Kotlin Code Sorter",
//                NotificationDisplayType.BALLOON,
//                false)
//        notificationGroup.createNotification("Android Kotlin Code Sorter",
//                "Sorted",
//                NotificationType.INFORMATION,
//                null)
//                .notify(event.project)
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