package me.hana.docs.data

import kotlin.reflect.KClass

data class Parameter<T: Any>(
    internal var type: KClass<T>? = null,
    internal var isRequired: Boolean = false,
    var description: String = "",
    var sample: T? = null
)

fun <T: Any>Parameter<T>.isRequired() {
    isRequired = true
}