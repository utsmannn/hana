package me.hana.docs.data

import kotlin.reflect.KClass

data class Parameter<T: Any>(
    internal var type: KClass<T>? = null,
    var description: String = "",
    var sample: T? = null,
    internal var isRequired: Boolean = false
) {
    fun isRequired() {
        isRequired = true
    }
}