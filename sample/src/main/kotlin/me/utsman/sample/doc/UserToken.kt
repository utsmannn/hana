package me.utsman.sample.doc

import me.hana.docs.annotation.DocFieldDescription

data class UserToken(
    @DocFieldDescription("Token of user")
    var token: String = ""
)