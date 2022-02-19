package me.hana.docs.data.configuration

data class Configuration(
    var title: String = "",
    var description: String = "",
    var github: String = "",
    var postman: String = "",
    var host: String = "0.0.0.0",
    var path: String = "/docs",
    var author: String = "",
    var enableJsonSampleObject: Boolean = true
)
