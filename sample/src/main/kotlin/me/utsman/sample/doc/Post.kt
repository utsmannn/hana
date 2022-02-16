package me.utsman.sample.doc

import me.hana.docs.annotation.DocFieldDescription

data class Post(
    @DocFieldDescription("Id of post")
    var id: String = "",
    @DocFieldDescription("Title of post")
    var title: String = "",
    @DocFieldDescription("Image url of post")
    var imageUrl: String = ""
)