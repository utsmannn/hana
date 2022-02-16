package me.utsman.sample.doc

import me.hana.docs.annotation.DocFieldDescription

data class UserRequest(
    @DocFieldDescription("Username of user", isRequired = true)
    var username: String = "",
    @DocFieldDescription("Password", isRequired = true)
    var password: String = ""
)