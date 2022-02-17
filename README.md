<h1 align="center">
  Hana
</h1>

<p align="center">
  <img src="https://media2.giphy.com/media/nR4L10XlJcSeQ/giphy.gif?cid=ecf05e47yjoyj2xhet0odajgs9fkwy2w12dzl2nee3pk6q0m&rid=giphy.gif&ct=g"/>
</p>

<p align="center">
  <a href="https://jitpack.io/#utsmannn/hana"><img alt="Jitpack" src="https://jitpack.io/v/utsmannn/hana.svg"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"></a>
  <a href="https://github.com/utsmannn/hana/pulls"><img alt="Pull request" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat"></a>
  <a href="https://ktor.io/docs/eap/welcome.html"><img alt="Kotlin Ktor" src="https://img.shields.io/badge/Ktor-%5E2.0.0--beta-red"></a>
  <a href="https://twitter.com/utsmannn"><img alt="Twitter" src="https://img.shields.io/twitter/follow/utsmannn"></a>
  <a href="https://github.com/utsmannn"><img alt="Github" src="https://img.shields.io/github/followers/utsmannn?label=follow&style=social"></a>
  <h3 align="center">Rest documentation plugin for Kotlin Ktor</h3>
</p>


|![](https://i.ibb.co/xsB1Rn7/ss3.png)|![](https://i.ibb.co/TRpFn4C/ss4.png)|
|--|--|

# Disclaimer
**This plugin only support for Kotlin Ktor 2.0.0-beta or later.**

# Live demo
Live of `sample` module can be found here https://hanadoc.herokuapp.com/doc

# Download

Repository
```kts
maven { url = uri("https://www.jitpack.io") }
```
Dependencies
```kts
implementation("com.github.utsmannn:hana:$hana_version")
```

# Implementation
## Install plugin
On your `io.ktor.server.application.Application` class, install Hana plugin, example:
```kt
install(HanaDocs) {
    title = "Lorem ipsum dolor sit amet"
    author = "Muhammad utsman"
    path = "/anudocs" // default is '/docs'
    github = "https://github.com/utsmannn"

    // if needed
    postman = "https://app.getpostman.com/run-collection/12345qwert"

    // markdown support
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
```



## Setup routing documentation
### Config class
All config on the `EndPoint`, `Parameter` and `DocFieldDescription` class

***Endpoint*** (`me.hana.docs.endpoint.EndPoint`)
| param | type | desc |
|---|---|---|
|`description`| String | explain endpoint description |
|`pathParameter(String, KClass, Parameter)`| Extensions | add path parameter (title, kotlin class, invoke parameter) |
|`queryParameter(String, KClass, Parameter)`| Extensions | add query parameter (title, kotlin class, invoke parameter) |
|`headerParameter(String, KClass, Parameter)`| Extensions | add header parameter (title, kotlin class, invoke parameter) |
|`multipartParameter(String, KClass, Parameter)`| Extensions | add multipart parameter (title, kotlin class, invoke parameter) |
|`bodyParameter(KClass, Parameter)`| Extensions | add body parameter (kotlin class, invoke parameter) |


***Parameter*** (`me.hana.docs.data.Parameter`)
| param | type | desc |
|---|---|---|
|`description`| String | explain parameter description |
|`sample`| Object | add sample data, the data will be rendered as json |
|`isRequired()`| Extensions | make the parameter is required, red asterisk |


***Field Annotation*** (`me.hana.docs.annotation.DocFieldDescription`)<br>
The `DocFieldDescription` used by data object when you rendered in documentation
| param | type | desc |
|---|---|---|
|`description`| String | explain field description |
|`isRequired`| Boolean | make the field is required, red asterisk |


***Route Extensions***<br>
For the implemention of route, you must used `hanaDocsParent` and `hanaDocs`. They are method for generating `EndPoint` class config.
| param | type | desc |
|---|---|---|
|`hanaDocsParent`| Extensions | setup the parent of route, this extensions used in route without method (etc. `route("\blabla")`) |
|`hanaDocs`| Extensions | setup the method of route, this extensions used in route with method (etc. `get("\kursi")` |


## Sample
Each route maybe need data class of render sample object like response, body, etc. Example of implementation:
```kt
route("/post") {
    // parent route description
    hanaDocsParent("Post", identifier = 2) {
        description = """
            Nulla fringilla, libero ut volutpat sollicitudin, arcu nibh consectetur quam, pulvinar efficitur nunc sem in magna. 
            Integer eleifend odio in dui dictum, vitae commodo sem tristique. 
            Donec pulvinar, odio ut condimentum convallis, leo orci iaculis nibh, a dictum dui lacus eu elit.
        """.trimIndent()

        headerParameter("Authorization", String::class) {
            isRequired()
            description = """
                        In ac sollicitudin leo. Ut sem nunc, pretium sed blandit id, placerat sit amet velit. Nulla et dolor vel nisl laoreet euismod interdum quis nisl.
                    """.trimIndent()

            sample = "qwertyuiop1234567asdfghjklzxcvbnm"
        }
    }

    // child route
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
            arrayOf(
                Post("1ae", "title1", "https://imageurl.com/image1.png"),
                Post("1sdf", "title2", "https://imageurl.com/image2.png"),
                Post("1sds", "title3", "https://imageurl.com/image2.png"),
                Post("1df", "title4", "https://imageurl.com/image2.png")
            )
        )
    }
```

See on fully sample on [sample](/sample) module

## Limitation
- Not supported for render `List`, use `Array` instead
- Not supported for render generic class
- and other, please create issue

---
```
Copyright 2020 Muhammad Utsman

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




