package io.github.peerapongsam.androidkotlinsorter.util

fun String.isTextBackingProperty(): Boolean {
    return this.contains(" = _")
            || this.contains("get()")
}

fun String.isNameBackingProperty(): Boolean {
    return this.startsWith("_")
}

private var _age: Int = 20
val age: Int
    get() {
        return _age
    }

val age5 = _age
val age2: Int = _age
val age3: Int get() = _age
val age4: Int
    get() = _age