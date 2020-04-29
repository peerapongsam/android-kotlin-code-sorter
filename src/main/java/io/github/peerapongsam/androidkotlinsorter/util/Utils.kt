package io.github.peerapongsam.androidkotlinsorter.util

import org.jetbrains.kotlin.descriptors.FunctionDescriptor

object Utils {

    fun getDefinedClass(descriptor: FunctionDescriptor?, parentList: MutableList<String> = mutableListOf()): String? {
        return if (descriptor == null) {
            parentList.firstOrNull()
        } else {
            if (descriptor.overriddenDescriptors.isNotEmpty()) {
                parentList.add(getQualifierName(descriptor.overriddenDescriptors.firstOrNull().toString()))
                getDefinedClass(descriptor.overriddenDescriptors.firstOrNull(), parentList)
            } else {
                parentList.firstOrNull()
            }
        }
    }

    fun getQualifierName(descriptor: String): String {
        return descriptor.replace(".+defined in (.+)\\[.+]".toRegex(), "$1")
    }
}