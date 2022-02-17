package me.hana.docs

import io.ktor.http.*
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import me.hana.docs.data.descriptor.FieldDescriptor
import me.hana.docs.data.descriptor.ParameterDescriptor
import me.hana.docs.endpoint.EndPoint

data class HtmlStringData(
    var title: String = "",
    var author: String = "",
    var url: String = "",
    var topMenu: String = "",
    var preamble: String = "",
    var sidebar: String = "",
    var content: String = "",
    var objectData: String = "",
) {
    companion object {
        fun fromHana(hanaDocs: HanaDocs): HtmlStringData {
            val sidebar = buildString {
                appendHTML().div {
                    hanaDocs.getGroup().forEach {
                        val title = it.name
                        val endPoint = it.child
                        div(classes = "title-group") {
                            a(href = "#${title.idLink()}") {
                                h4(classes = "link-none") {
                                    +title
                                }
                            }
                        }

                        ul(classes = "nav list-group") {
                            endPoint.forEach { endPoint ->
                                val isParent = endPoint.isParent
                                if (!isParent) {
                                    li {
                                        a(href = "#${endPoint.idLink()}") {
                                            i(classes = "fa-solid fa-code")
                                            +endPoint.title
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div(classes = "title-group") {
                        a(href = "#object") {
                            h4(classes = "link-none") {
                                +"Etc"
                            }
                        }
                    }

                    ul(classes = "nav list-group") {
                        li {
                            a(href = "#object") {
                                i(classes = "fa-solid fa-code")
                                +"Objects"
                            }
                        }
                    }
                }
            }

            val content = buildString {
                appendHTML().div {
                    hanaDocs.getGroup().forEach { group ->
                        val parentEndPoint = group.endPoint
                        val child = group.child

                        h2 {
                            id = parentEndPoint.idLink()
                            +parentEndPoint.title
                        }

                        markdown(parentEndPoint.description)
                        renderParameter(hanaDocs, parentEndPoint, hanaDocs.configuration.host, false)

                        child.forEach {
                            h3 {
                                id = it.idLink()
                                +it.title
                            }
                            val labelClass = "label label-pill label-" + when (HttpMethod.parse(it.method)) {
                                HttpMethod.Get -> "primary"
                                HttpMethod.Post -> "success"
                                HttpMethod.Put -> "warning"
                                HttpMethod.Patch -> "info"
                                HttpMethod.Delete -> "danger"
                                else -> "secondary"
                            }

                            div(classes = "breadcrumb") {
                                h4 {
                                    id = "method"
                                    span(classes = labelClass) {
                                        +it.method
                                    }
                                    +" "
                                    +it.path
                                }
                            }
                            markdown(it.description)
                            renderParameter(hanaDocs, it, hanaDocs.configuration.host)
                            renderResponse(fieldDescriptor = it.responseField, "Response")
                            hr { }
                            br { }
                        }

                    }
                }
            }

            val topMenuBody = buildString {
                val hasGithub = hanaDocs.configuration.github.isNotEmpty()
                val hasPostman = hanaDocs.configuration.postman.isNotEmpty()
                appendHTML().apply {
                    if (hasGithub) {
                        li {
                            a(href = hanaDocs.configuration.github, target = "blank") {
                                +"Github"
                            }
                        }
                    }

                    if (hasPostman) {
                        li {
                            a(href = hanaDocs.configuration.postman, target = "blank") {
                                +"Run on Postman"
                            }
                        }
                    }
                }
            }

            val preamble = if (hanaDocs.configuration.description.isNotEmpty()) {
                buildString {
                    appendHTML().div {
                        markdown(hanaDocs.configuration.description)
                        hr { }
                    }
                }
            } else {
                ""
            }

            val author = if (hanaDocs.configuration.author.isNotEmpty()) {
                buildString {
                    appendHTML().p {
                        +hanaDocs.configuration.author
                    }
                }
            } else {
                ""
            }

            val objectData = buildString {
                appendHTML().div {
                    renderObject(hanaDocs)
                    div {
                        hr {  }
                        a(href = "https://github.com/utsmannn/hana-ktordoc", target = "blank") {
                            h5 {
                                +"Generated by HanaDoc"
                            }
                        }
                    }
                }
            }

            return HtmlStringData(
                title = hanaDocs.configuration.title,
                author = author,
                url = hanaDocs.configuration.host,
                topMenu = topMenuBody,
                preamble = preamble,
                sidebar = sidebar,
                content = content,
                objectData = objectData
            )
        }

        private fun FlowContent.codeBlock(code: String, language: String = "") {
            pre("alert bg-light") {
                code {
                    if (language.isNotEmpty()) {
                        lang = ""
                    }
                    +code
                }
            }
        }

        private fun FlowContent.renderResponse(fieldDescriptor: FieldDescriptor, title: String) {
            val hasParameter = fieldDescriptor != FieldDescriptor()
            val hasJson = fieldDescriptor.jsonString.isNotEmpty()

            val parameter = {
                val member = fieldDescriptor.member
                renderTableField(member)
            }
            val json = {
                if (fieldDescriptor.jsonString.isNotEmpty()) {
                    h5 {
                        +"Sample"
                    }
                    codeBlock(fieldDescriptor.jsonString, "json")
                }
            }

            if (!hasParameter && !hasJson) {
                return
            }
            br { }
            h4 {
                +title
            }

            if (hasParameter) {
                parameter.invoke()
            }

            if (hasJson) {
                json.invoke()
            }

        }

        private fun FlowContent.renderObject(hanaDocs: HanaDocs) {
            val members = hanaDocs.getAllObject()
            br { }
            h1 {
                id = "object"
                +"Objects"
            }
            hr { }
            br { }
            members.forEach { (id, childMember) ->
                val childValid = childMember.distinctBy { it.name }
                h4 {
                    this.id = id.idTypeOf()
                    +id
                }
                renderTableField(childValid)
                br { }
            }
        }

        private fun FlowContent.renderParameter(
            hanaDocs: HanaDocs,
            endPoint: EndPoint,
            host: String,
            withSample: Boolean = true
        ) {
            val params = endPoint.params
            val path = endPoint.path
            val method = endPoint.method

            val objectAvailable = hanaDocs.getAllObject().map { it.value }.flatten()

            val isObjectData: (type: String) -> Boolean = {
                objectAvailable.map { obj -> obj.objectId }.contains(it)
            }

            val renderTable = {
                table(classes = "table") {
                    thead {
                        tr {
                            th(scope = ThScope.col) {
                                +"Parameter"
                            }
                            th(scope = ThScope.col) {
                                +"Type"
                            }
                            th(scope = ThScope.col) {
                                +"Description"
                            }
                            th(scope = ThScope.col) {
                                +"Location"
                            }
                        }
                    }
                    tbody {
                        params.forEach { (key, param) ->
                            tr {
                                th(scope = ThScope.row) {
                                    +key
                                    if (param.isRequired) {
                                        span(classes = "required") {
                                            +"*"
                                        }
                                    }

                                }
                                td {
                                    val type = param.simpleType
                                    if (isObjectData.invoke(type)) {
                                        a("#${type.idTypeOf()}") {
                                            +type
                                        }
                                    } else {
                                        +type
                                    }
                                }
                                td {
                                    +param.desc
                                }
                                td {
                                    +param.location.name
                                }
                            }
                        }
                    }
                }
            }

            val parameterPath = params.filter { it.value.location == ParameterDescriptor.Location.PATH }
            val parameterQuery = params.filter { it.value.location == ParameterDescriptor.Location.QUERY }
            val parameterHeader = params.filter { it.value.location == ParameterDescriptor.Location.HEADER }
            val parameterBody = params.filter { it.value.location == ParameterDescriptor.Location.BODY }
            val parameterMultipart = params.filter { it.value.location == ParameterDescriptor.Location.MULTIPART }

            val space = " ".repeat(2)
            val hostValid = if (host.contains("http")) {
                "$host/$path".replace("//", "/")
            } else {
                "https://$host/$path".replace("//", "/")
            }

            val hasSamplePath = parameterPath
                .filter { it.value.sample.isNotEmpty() }.map { it.value.sample }.isNotEmpty()

            val hasSampleQuery = parameterQuery
                .filter { it.value.sample.isNotEmpty() }.map { it.value.sample }.isNotEmpty()

            val hasSampleHeader = parameterHeader
                .filter { it.value.sample.isNotEmpty() }.map { it.value.sample }.isNotEmpty()

            val hasSampleBody = parameterBody
                .filter { it.value.sample.isNotEmpty() }.map { it.value.sample }.isNotEmpty()

            val hasSampleMultipart = parameterMultipart
                .filter { it.value.sample.isNotEmpty() }.map { it.value.sample }.isNotEmpty()

            val hasParam = params.isNotEmpty()
            if (!hasParam) {
                return
            }

            val samplePath = parameterPath.run {
                var sample = "/"
                map { it.key to it.value.sample }.forEach {
                    sample += if (hasSamplePath) {
                        "/${it.second}"
                    } else {
                        "/:${it.first}"
                    }
                }
                sample.replace("//", "/")
            }

            val sampleQuery = parameterQuery.run {
                var sample = "?"
                map { it.key to it.value.sample }.forEach {
                    sample += if (hasSampleQuery) {
                        "&${it.first}=${it.second}"
                    } else {
                        "&${it.first}={${it.first}}"
                    }
                }
                sample.replace("?&", "?")
            }

            val sampleHeader = parameterHeader.map { it.key to it.value.sample }.run {
                var sample = ""
                if (hasSampleHeader) {
                    forEach {
                        sample += "$space-H '${it.first}: ${it.second}' \\\n"
                    }
                }
                sample
            }

            val sampleBody = parameterBody.map { it.key to it.value.sample }.run {
                var sample = ""
                if (hasSampleBody) {
                    firstOrNull()?.let {
                        sample += "$space-d '${it.second}' \\\n"
                    }
                }
                sample
            }

            val sampleMultipart = parameterMultipart.map { it.key to it.value.sample }.run {
                var sample = ""
                if (hasSampleMultipart) {
                    forEach {
                        sample += "$space-F '${it.first}:${it.second}' \\\n"
                    }
                }
                sample
            }

            val sampleUrl = "$hostValid$samplePath$sampleQuery"
                .removeSuffix("?")
                .removeStringTag()
                .replaceSpaceUrl()

            val sampleTemplate = "curl -X $method \\\n" +
                    sampleHeader +
                    sampleBody +
                    sampleMultipart +
                    "$space$sampleUrl"

            br { }
            h4 {
                +"Parameter"
            }
            renderTable.invoke()
            if (withSample) {
                h5 {
                    +"Sample"
                }
                codeBlock(sampleTemplate)
            }
        }

        private fun EndPoint.idLink(): String {
            return title.replace(" ", "-")
        }

        private fun String.idLink(): String {
            return replace(" ", "-")
        }
    }
}

fun FlowContent.renderTableField(member: List<FieldDescriptor.MemberFieldDescriptor>) {
    if (member.isNotEmpty()) {
        table(classes = "table") {
            thead {
                tr {
                    th(scope = ThScope.col) {
                        +"Parameter"
                    }
                    th(scope = ThScope.col) {
                        +"Type"
                    }
                    th(scope = ThScope.col) {
                        +"Description"
                    }
                }
            }
            tbody {
                member.forEach { field ->
                    tr {
                        th(scope = ThScope.row) {
                            +field.name
                            if (field.isRequired) {
                                span(classes = "required") {
                                    +"*"
                                }
                            }
                        }
                        td {
                            if (field.idOfType.isNotEmpty()) {
                                a("#${field.idOfType}") {
                                    +field.type
                                }
                            } else {
                                +field.type
                            }
                        }
                        td {
                            +field.description
                        }
                    }
                }
            }
        }
    }
}