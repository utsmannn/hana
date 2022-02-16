package me.hana.docs.data

import kotlin.reflect.KClass

data class Parameter<T: Any>(
    internal var type: KClass<T>? = null,
    var isRequired: Boolean = false,
    var description: String = "",
    var sample: T? = null
) {
    fun isRequired() {
        isRequired = true
    }

}

fun <T: Any>Parameter<T>.isRequireds() {
    isRequired = true
}