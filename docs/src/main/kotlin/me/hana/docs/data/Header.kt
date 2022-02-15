package me.hana.docs.data

data class Header(
    var isRequiredAuth: Boolean = false,
    var key: String,
    var value: String = "",
    var description: String = "",
    var sample: String = ""
)
