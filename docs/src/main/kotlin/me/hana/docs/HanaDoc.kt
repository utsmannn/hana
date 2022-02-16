package me.hana.docs

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.hana.docs.data.configuration.Configuration
import me.hana.docs.data.descriptor.FieldDescriptor
import me.hana.docs.endpoint.EndPoint
import me.hana.docs.endpoint.EndPointGroup

class HanaDocs(val configuration: Configuration) {
    private val endPoints: MutableList<EndPoint> = mutableListOf(EndPoint.other())
    private val parents: MutableList<String> = mutableListOf()

    fun addEndPoint(route: Route, endPoint: EndPoint) {
        val path = route.toStringPath()
        val method = route.toStringMethod()
        val parent = route.parent?.toStringPath().orEmpty()
        val isParent = endPoint.isParent
        if (isParent) parents.add(parent)

        endPoint.apply {
            this.path = path
            this.method = method
            this.isParent = isParent
        }

        endPoints.add(endPoint)
    }

    fun getGroup(): List<EndPointGroup> {
        val groupEndPoint = endPoints.filter { it.isParent }
        val newEndPoints = endPoints.filter { !it.isParent }

        val data = groupEndPoint.map { group ->
            val child = newEndPoints.filter { it.parent == group.path }.sortedBy { it.priority }
            EndPointGroup(
                name = group.title,
                endPoint = group,
                child = child,
                priority = group.priority
            )
        }.sortedBy { it.priority }

        return data
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

fun Route.hanaDocs(title: String, parent: String = "", endPoint: EndPoint.() -> Unit = {}) {
    val endPointInstance = EndPoint().apply(endPoint)
    val hana = application.plugin(HanaDocs)
    endPointInstance.title = title
    endPointInstance.isParent = false
    endPointInstance.parent = parent
    hana.addEndPoint(this, endPointInstance)
}

fun Route.hanaDocsParent(title: String, parent: String = "", endPoint: EndPoint.() -> Unit = {}) {
    val endPointInstance = EndPoint().apply(endPoint)
    val hana = application.plugin(HanaDocs)
    endPointInstance.title = title
    endPointInstance.isParent = true
    endPointInstance.parent = parent
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
        ""
    }

    return path
}