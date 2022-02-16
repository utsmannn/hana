package me.utsman.sample.doc

import me.hana.docs.annotation.DocFieldDescription

data class User(
    @DocFieldDescription("Id of user")
    var id: String = "",
    @DocFieldDescription("Username of user")
    var username: String = "",
    @DocFieldDescription("Image url of user")
    var imageUrl: String = ""
)