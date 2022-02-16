package me.hana.docs.endpoint

import me.hana.docs.*
import me.hana.docs.data.DocFile
import me.hana.docs.data.descriptor.FieldDescriptor
import me.hana.docs.data.descriptor.ParameterDescriptor
import me.hana.docs.data.Parameter
import me.hana.docs.lastOfPackage
import me.hana.docs.removeNullString
import me.hana.docs.toJsonString
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmName

data class EndPoint(
    var path: String = "",
    var description: String = "",
    internal var title: String = "",
    internal var identifier: Int = -1,
    internal var method: String = "",
    internal var priority: Int = 100,
    internal var params: HashMap<String, ParameterDescriptor> = hashMapOf(),
    internal var header: HashMap<String, ParameterDescriptor> = hashMapOf(),
    internal var response: String = "",
    internal var responseField: FieldDescriptor = FieldDescriptor(),
    internal var requestField: FieldDescriptor = FieldDescriptor(),
    internal var request: String = "",
    internal var isParent: Boolean = false,
    internal var childs: List<String> = emptyList(),
    internal var parentIdentifier: Int = -1
) {

    internal fun getAllObjectMember(): Map<String, List<FieldDescriptor.MemberFieldDescriptor>> {
        val objectIncludedInParam = params.map { it.value.type }.mapNotNull {
            try {
                val kClass = Class.forName(it).kotlin
                FieldDescriptor.MemberFieldDescriptor.of(kClass).member
            } catch (e: ClassNotFoundException) {
                null
            }
        }.flatten()

        val mainGrouping = (responseField.objectIncluded + requestField.objectIncluded + objectIncludedInParam).groupBy { it.objectId }
        val listGroup = mainGrouping.toList().distinct().map {
            val validList = it.second.distinctBy { s -> s.name }
            validList
        }.flatten()

        return listGroup.groupBy { it.objectId }
    }

    companion object {
        fun other(): EndPoint {
            return EndPoint(title = "Other", isParent = true)
        }
    }
}

fun <T: Any> EndPoint.pathParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    paramOf(name, ParameterDescriptor.Location.PATH, clazz, param)
}

fun <T: Any> EndPoint.queryParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    paramOf(name, ParameterDescriptor.Location.QUERY, clazz, param)
}

fun <T: Any> EndPoint.headerParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    paramOf(name, ParameterDescriptor.Location.HEADER, clazz, param)
}

fun <T: Any> EndPoint.multipartParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    paramOf(name, ParameterDescriptor.Location.MULTIPART, clazz, param)
}

fun <T: Any> EndPoint.bodyParameter(clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    paramOf("body", ParameterDescriptor.Location.BODY, clazz, param)
}

fun EndPoint.setPriority(priority: Int) {
    this.priority = priority
}

private fun <T: Any> EndPoint.paramOf(name: String, location: ParameterDescriptor.Location, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    val parameter = Parameter<T>().apply(param)
    val type = if (clazz.isDocFile()) {
        "File"
    } else {
        FieldDescriptor.MemberFieldDescriptor.of(clazz).type
    }
    val simpleType = if (clazz.isDocFile()) {
        "File"
    } else {
        clazz.simpleName?.lastOfPackage().orEmpty()
    }
    val sample: (Boolean) -> String = {
        if (clazz.isDocFile()) {
            "@${(parameter.sample as DocFile).fileName}"
        } else {
            parameter.sample.toJsonString(it).removeNullString()
        }
    }


    val paramDescriptor = ParameterDescriptor(
        name = name,
        desc = parameter.description,
        type = type,
        simpleType = simpleType,
        sample = sample.invoke(false),
        sampleBeauty = sample.invoke(true),
        location = location
    )
    params[name] = paramDescriptor
}

fun EndPoint.responseDoc(data: String) {
    response = data
}

fun EndPoint.bodyDoc(data: String) {
    request = data
}

fun EndPoint.sampleBody(data: Any) {
    response = data.toJsonString()
}

fun EndPoint.responseSample(data: Any) {
    val fieldDescriptor = FieldDescriptor.of(data)
    responseField = fieldDescriptor
}

fun EndPoint.requestSample(data: Any) {
    val fieldDescriptor = FieldDescriptor.of(data)
    requestField = fieldDescriptor
}