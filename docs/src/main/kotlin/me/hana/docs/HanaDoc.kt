package me.hana.docs

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.hana.docs.data.configuration.Configuration
import me.hana.docs.data.descriptor.FieldDescriptor

class HanaDocs(val configuration: Configuration) {
    val endPoints: MutableList<EndPoint> = mutableListOf()

    fun addEndPoint(route: Route, endPoint: EndPoint) {
        val path = route.toStringPath()
        val method = route.toStringMethod()
        endPoint.apply {
            this.path = path
            this.method = method
        }

        endPoints.add(endPoint)
    }

    fun getAllObject(): Map<String, List<FieldDescriptor.MemberFieldDescriptor>> {
        val objects = endPoints.map {
            it.getAllObjectMember().map {
                    obj -> obj.value
            }.flatten()
        }.flatten()

        return objects.groupBy { it.objectId }
    }

    companion object : ApplicationPlugin<Application, Configuration, HanaDocs> {
        override val key: AttributeKey<HanaDocs>
            get() = AttributeKey("Hana docs")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): HanaDocs {
            val configuration = Configuration()
            configure.invoke(configuration)
            val hanaDocs = HanaDocs(configuration)

            pipeline.install(FreeMarker) {
                templateLoader = ClassTemplateLoader(this::class.java.classLoader, "simple")
            }

            pipeline.routing {
                get(configuration.path) {
                    val data = HtmlStringData.fromHana(hanaDocs)
                    call.respond(FreeMarkerContent("index.ftl", mapOf("data" to data)))
                }
            }
            return hanaDocs
        }
    }
}

fun Route.hanaDocs(endPoint: EndPoint.() -> Unit) {
    val endPointInstance = EndPoint().apply(endPoint)
    val hana = application.plugin(HanaDocs)
    hana.addEndPoint(this, endPointInstance)
}

internal fun Route.toStringPath(): String {
    val rawString = toString()
    val path = if (rawString.contains("/(")) {
        val splitPath = rawString.split("/(")
        splitPath.getOrNull(0).orEmpty()
    } else {
        rawString
    }

    return path
}

internal fun Route.toStringMethod(): String {
    val rawString = toString()
    val path = if (rawString.contains("method:")) {
        val splitPath = rawString.split("method:")
        splitPath.getOrNull(splitPath.lastIndex).orEmpty().removeSuffix(")")
    } else {
        rawString
    }

    return path
}