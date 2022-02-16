package me.utsman.sample

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.hana.docs.*
import me.hana.docs.annotation.DocFieldDescription
import me.hana.docs.data.DocFile
import me.hana.docs.endpoint.*
import java.io.File

data class AnuanResponse(
    @DocFieldDescription("data of response")
    val data: String = "",
    @DocFieldDescription("status of response", isRequired = true)
    val status: Boolean = true,
    @DocFieldDescription("body of response")
    val anuans: List<AnuanBody> = emptyList()
)

data class AnuanBody(
    @DocFieldDescription("data anuan nya ya")
    val dataanu: String = "",
    @DocFieldDescription("status anuan nya yaaaa")
    val statusanu: Boolean = true,
    @DocFieldDescription("yah elaah")
    val cihuy: AduuhBody = AduuhBody()
)

data class AduuhBody(
    @DocFieldDescription("duh ilah yaa", isRequired = true)
    val duuh: String = "",
    @DocFieldDescription("anuannya nih")
    val yesanu: String = ""
)

data class Wew(
    @DocFieldDescription("hiyaaa")
    val hiyaa: String = "",
    val data: Cuk = Cuk()
) {
    data class Cuk(
        @DocFieldDescription("cuk")
        val cuk: String = ""
    )
}

fun Application.configureRoute() {
    install(HanaDocs) {
        title = "Sample doc ya ya yaaaa"
        path = "/haduh"
        github = "https://github.com/utsmannn"
        postman = "https://app.getpostman.com/run-collection/65a1ff2b518c8e46615b"
        author = "Muhammad utsman"
        description = """
            This is documentation for sample HanaDoc
        """.trimIndent()
    }

    routing {

        route("/user") {
            hanaDocs("Hiyaa") {
                description = "user yaa hh parent nye"
                setPriority(1)
                headerParameter("Auth", String::class) {
                    description = "nah ini yaaaa"
                    sample = "hmmmmmm"
                    isRequired()
                }
            }

            get {
                call.respond(Wew())
            }.hanaDocs("user") {
                description = "hiyaaaa"

                multipartParameter("name", String::class) {
                    description = "this is name of file"
                    sample = "haduh"
                }

                multipartParameter("image", DocFile::class) {
                    description = "this is file"
                    sample = DocFile("anu.jpg")
                }

                queryParameter("id", String::class) {
                    description = "id of product"
                    sample = "ukdghf784"
                }
            }
        }

        get("/hai") {
            call.respond("Duuh")
        }.hanaDocs("Hai") {
            description = desc1

            responseSample(Wew())

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
        }.hanaDocs("Cuk") {
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

            responseSample(
                MainResponse.bindToResponse(AnuanBody(), "cuk")
            )
        }

        post("/cuuk") {
            call.respond("asas")
        }.hanaDocs("cuk2") {
            description = desc5

            queryParameter("wahh", String::class) {
                description = "nah ini"
            }
        }

        route("/awer") {
            get("/hmmmm") {
                call.respond("hmmm")
            }.hanaDocs("tekewer") {
                description = desc4
            }
        }
    }
}