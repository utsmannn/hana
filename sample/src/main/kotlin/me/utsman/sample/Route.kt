package me.utsman.sample

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.hana.docs.*
import me.hana.docs.annotation.DocDescription

data class AnuanResponse(
    @DocDescription("data of response")
    val data: String = "",
    @DocDescription("status of response", isRequired = true)
    val status: Boolean = true,
    @DocDescription("body of response")
    val anuans: List<AnuanBody> = emptyList()
)

data class AnuanBody(
    @DocDescription("data anuan nya ya")
    val dataanu: String = "",
    @DocDescription("status anuan nya yaaaa")
    val statusanu: Boolean = true,
    @DocDescription("yah elaah")
    val cihuy: AduuhBody = AduuhBody()
)

data class AduuhBody(
    @DocDescription("duh ilah yaa", isRequired = true)
    val duuh: String = "",
    @DocDescription("anuannya nih")
    val yesanu: String = ""
)

fun Application.configureRoute() {
    install(HanaDocs) {
        title = "Sample doc"
        path = "/haduh"
        github = "https://github.com/utsmannn"
        author = "Muhammad utsman"
        description = """
            This is documentation for sample HanaDoc
        """.trimIndent()
    }

    routing {
        get("/hai") {
            call.respond("Duuh")
        }.hanaDocs {
            title = "Haiii"
            description = desc1

            responseDescriptor(AnuanResponse())

            pathParameter("id", String::class) {
                description = "id of hai ya"
                sample = "sha2983yunsdf"
            }

            pathParameter("message", String::class) {
                isRequired()
                description = "message of hai ya"
                sample = "hm aduhh"
            }

            queryParameter("page", Int::class) {
                description = "page of hai ini yaa"
                sample = 3
            }

            bodyParameter(AduuhBody::class) {
                description = "hmmm body nya"
                sample = AduuhBody()
            }
        }

        get("/cuk") {
            call.respond("Cukk")
        }.hanaDocs {
            title = "Cuk"
            description = desc2

            pathParameter("iyaaa", String::class) {
                description = "iya cuk"
                sample = "naon"
            }

            headerParameter("Authorization", String::class) {
                description = "token nya yaa"
                sample = "dsfkhruisgt78i5hi34"
                isRequired()
            }

            responseDescriptor(AnuanBody())
        }

        post("/cuuk") {
            call.respond("asas")
        }.hanaDocs {
            title = "cuuuk"
            description = desc5

            queryParameter("wahh", String::class) {
                description = "nah ini"
            }
        }

        route("/awer") {
            get("/hmmmm") {
                call.respond("hmmm")
            }.hanaDocs {
                title = "awer"
                description = desc4
            }
        }

    }
}