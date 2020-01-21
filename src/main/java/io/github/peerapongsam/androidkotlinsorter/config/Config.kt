package io.github.peerapongsam.androidkotlinsorter.config

data class Config(
        val isOrderPropertiesByName: Boolean = false,
        val isOrderActivityLifeCycle: Boolean = false,
        val isOrderFunctionsByName: Boolean = false,
        val isOrderInnerClassesByName: Boolean = false
)