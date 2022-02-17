package me.utsman.sample

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.hana.docs.HanaDocs
import me.hana.docs.data.DocFile
import me.hana.docs.data.isRequired
import me.hana.docs.endpoint.*
import me.hana.docs.hanaDocs
import me.hana.docs.hanaDocsParent
import me.utsman.sample.doc.*

fun Application.configureRoute() {
    install(HanaDocs) {
        title = "Lorem ipsum dolor sit amet"
        path = "/doc"
        github = "https://github.com/utsmannn"
        postman = "https://app.getpostman.com/run-collection/65a1ff2b518c8e46615b"
        author = "Muhammad utsman"
        description = """
            ## Rest Documentation
            
            Consectetur adipiscing elit. Nulla ut sem non neque tincidunt placerat tincidunt in justo. Maecenas ultricies condimentum porttitor.
            
            <p align="center">
              <img src="https://i.ibb.co/YdvBMMG/hiya.gif"/>
            </p>
            
            - Ut augue justo
            - dignissim id lectus non
            - imperdiet imperdiet sem
            
            ```kotlin
            implementation("com.github.utsmannn:hana:version")
            ```
        """.trimIndent()
    }

    val requiredAuthorization: (EndPoint) -> Unit = {
        it.run {
            headerParameter("Authorization", String::class) {
                isRequired()
                description = """
                            In ac sollicitudin leo. Ut sem nunc, pretium sed blandit id, placerat sit amet velit. Nulla et dolor vel nisl laoreet euismod interdum quis nisl.
                        """.trimIndent()

                sample = "qwertyuiop1234567asdfghjklzxcvbnm"
            }
        }
    }

    routing {
        route("v1") {
            route("/user") {
                hanaDocsParent("User", identifier = 1) {
                    description = """
                        Phasellus porta diam gravida eros euismod, vel molestie quam pretium. [Morbi](https://github.com/utsmannn) iaculis ex eu imperdiet ullamcorper. Vestibulum id turpis eros. 
                        Nunc eu lacus varius, feugiat nibh ut, tempor magna. Proin efficitur eget est sed fermentum. *Maecenas* dignissim ultricies massa..
                    """.trimIndent()
                }

                post("/register") {
                    val body = call.receive<UserRequest>()
                    // your logic
                    call.respond(User())
                }.hanaDocs("Register", parent = 1) {
                    description = """
                        Integer sollicitudin tortor tellus, ut dapibus odio *elementum* ut.
                    """.trimIndent()

                    bodyParameter(UserRequest::class) {
                        isRequired()
                        description = """
                            Ut vitae ipsum felis. Nulla interdum odio enim, quis tempor turpis convallis quis
                        """.trimIndent()
                        sample = UserRequest(
                            username = "username123",
                            password = "utsmanganteng123"
                        )
                    }

                    responseSample(
                        User(
                            id = "123",
                            username = "username123",
                            imageUrl = "https://imageurl.com/image.png"
                        )
                    )
                }

                post("/login") {
                    call.respond(UserToken())
                }.hanaDocs("Login", parent = 1) {
                    description = """
                        Sed iaculis lacus nunc, vel maximus dui suscipit eu. Aliquam vel aliquet diam, quis ornare ex.
                    """.trimIndent()

                    bodyParameter(UserRequest::class) {
                        isRequired()
                        description = """
                            Nullam imperdiet urna sodales
                        """.trimIndent()
                        sample = UserRequest(
                            username = "username123",
                            password = "utsmanganteng123"
                        )
                    }

                    responseSample(
                        UserToken("qwertyuiop1234567asdfghjklzxcvbnm")
                    )
                }

                post("/image") {
                    call.respond(User())
                }.hanaDocs("Add image profile", parent = 1) {
                    description = """
                        Ut tincidunt, elit eget pellentesque maximus, ipsum risus accumsan felis, rhoncus lobortis ligula nulla sed lacus
                    """.trimIndent()

                    requiredAuthorization(this)

                    multipartParameter("image", DocFile::class) {
                        description = "Aliquam at tortor et elit convallis consequat a et leo"
                        sample = DocFile("somefile.png")
                        isRequired()
                    }

                    multipartParameter("filename", String::class) {
                        description = "Praesent eget augue eget augue ornare tempus."
                        sample = "somefile.png"
                    }
                }
            }

            route("/post") {
                hanaDocsParent("Post", identifier = 2) {
                    description = """
                        Nulla fringilla, libero ut volutpat sollicitudin, arcu nibh consectetur quam, pulvinar efficitur nunc sem in magna. 
                        Integer eleifend odio in dui dictum, vitae commodo sem tristique. 
                        Donec pulvinar, odio ut condimentum convallis, leo orci iaculis nibh, a dictum dui lacus eu elit.
                    """.trimIndent()

                    requiredAuthorization(this)
                }

                get {
                    call.respond(listOf(Post()))
                }.hanaDocs("Get Posts", parent = 2) {
                    description = """
                        Aliquam erat volutpat. Pellentesque ornare, augue at consectetur fringilla, tortor erat rutrum magna, a commodo nisl mauris a nisi
                    """.trimIndent()

                    pathParameter("page", Int::class) {
                        description = "Cras elit sapien"
                        sample = 2
                    }

                    queryParameter("user_id", String::class) {
                        description = "laoreet sed odio in"
                        sample = "123"
                    }

                    responseSample(
                        PostResponse(
                            data = listOf(
                                Post("1ae", "title1", "https://imageurl.com/image1.png"),
                                Post("1sdf", "title2", "https://imageurl.com/image2.png"),
                                Post("1sds", "title3", "https://imageurl.com/image2.png"),
                                Post("1df", "title4", "https://imageurl.com/image2.png")
                            )
                        )
                    )
                }

                post {
                    val post = call.receive<Post>()
                    call.respond(Post())
                }.hanaDocs("Create posting", parent = 2) {
                    description = """
                            Nulla feugiat nibh quis massa hendrerit, id semper sapien suscipit. Morbi consequat massa purus. Nunc egestas lectus a pharetra commodo.
                        """.trimIndent()

                    bodyParameter(Post::class) {
                        description = "Phasellus faucibus odio metus, sed commodo erat vulputate eu. Sed efficitur posuere neque at bibendum."
                    }

                    responseSample(Post("1ae", "title1", "https://imageurl.com/image1.png"))
                }
            }
        }
    }
}