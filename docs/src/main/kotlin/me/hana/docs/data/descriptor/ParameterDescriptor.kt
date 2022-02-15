package me.hana.docs.data.descriptor

data class ParameterDescriptor(
    var name: String = "",
    var desc: String = "",
    var type: String = "",
    var sample: String = "",
    var sampleBeauty: String = "",
    var isRequired: Boolean = false,
    var location: Location = Location.PATH
) {

    enum class Location {
        PATH, QUERY, HEADER, BODY
    }
}