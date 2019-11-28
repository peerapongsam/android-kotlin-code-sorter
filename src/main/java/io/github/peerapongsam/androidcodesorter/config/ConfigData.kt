package io.github.peerapongsam.androidcodesorter.config

data class ConfigData(
        var declarationsOrders: Array<String>,
        var propertiesOrders: Array<String>,
        var keepValVarTogether: Boolean,
        var keepLateinitVarTogetherWithValVar: Boolean,
        var keepLazyInitializeTogetherWithVal: Boolean)