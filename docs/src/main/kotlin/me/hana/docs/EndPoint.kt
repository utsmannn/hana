package me.hana.docs

import me.hana.docs.data.descriptor.FieldDescriptor
import me.hana.docs.data.descriptor.ParameterDescriptor
import me.hana.docs.data.Parameter
import kotlin.reflect.KClass

data class EndPoint(
    var path: String = "",
    var method: String = "",
    var title: String = "",
    var description: String = "",
    internal var params: HashMap<String, ParameterDescriptor> = hashMapOf(),
    internal var header: HashMap<String, ParameterDescriptor> = hashMapOf(),
    internal var response: String = "",
    internal var responseField: FieldDescriptor = FieldDescriptor(),
    internal var requestField: FieldDescriptor = FieldDescriptor(),
    internal var request: String = ""
) {

    internal fun getAllObjectMember(): Map<String, List<FieldDescriptor.MemberFieldDescriptor>> {
        val mainGrouping = (responseField.objectIncluded + requestField.objectIncluded).groupBy { it.objectId }
        val listGroup = mainGrouping.toList().distinct().map {
            val validList = it.second.distinctBy { s -> s.name }
            validList
        }.flatten()

        return listGroup.groupBy { it.objectId }
    }
}

fun <T: Any> EndPoint.pathParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    val parameter = Parameter<T>().apply(param)
    val type = clazz.simpleName.orEmpty()
    val pathDescriptor = ParameterDescriptor(
        name = name,
        desc = parameter.description,
        type = type,
        sample = parameter.sample.toJsonString(false).removeNullString(),
        sampleBeauty = parameter.sample.toJsonString().removeNullString(),
        location = ParameterDescriptor.Location.PATH
    )
    params[name] = pathDescriptor
}

fun <T: Any> EndPoint.queryParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    val parameter = Parameter<T>().apply(param)
    val type = clazz.simpleName.orEmpty()
    val pathDescriptor = ParameterDescriptor(
        name = name,
        desc = parameter.description,
        type = type,
        sample = parameter.sample.toJsonString(false).removeNullString(),
        sampleBeauty = parameter.sample.toJsonString().removeNullString(),
        location = ParameterDescriptor.Location.QUERY
    )
    params[name] = pathDescriptor
}

fun <T: Any> EndPoint.headerParameter(name: String, clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    val parameter = Parameter<T>().apply(param)
    val type = clazz.simpleName.orEmpty()
    val pathDescriptor = ParameterDescriptor(
        name = name,
        desc = parameter.description,
        type = type,
        sample = parameter.sample.toJsonString(false).removeNullString(),
        sampleBeauty = parameter.sample.toJsonString().removeNullString(),
        location = ParameterDescriptor.Location.HEADER
    )
    params[name] = pathDescriptor
}

fun <T: Any> EndPoint.bodyParameter(clazz: KClass<T>, param: Parameter<T>.() -> Unit) {
    val parameter = Parameter<T>().apply(param)
    val type = clazz.simpleName.orEmpty()
    val pathDescriptor = ParameterDescriptor(
        name = "data",
        desc = parameter.description,
        type = type,
        sample = parameter.sample.toJsonString(false).removeNullString(),
        sampleBeauty = parameter.sample.toJsonString().removeNullString(),
        location = ParameterDescriptor.Location.BODY
    )
    params["data"] = pathDescriptor
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

fun EndPoint.responseDescriptor(data: Any) {
    val fieldDescriptor = FieldDescriptor.of(data)
    responseField = fieldDescriptor
}

fun EndPoint.requestDescriptor(data: Any) {
    val fieldDescriptor = FieldDescriptor.of(data)
    requestField = fieldDescriptor
}