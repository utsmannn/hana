package me.hana.docs.endpoint

data class EndPointGroup(
    val name: String = "",
    val endPoint: EndPoint = EndPoint(),
    val child: List<EndPoint> = emptyList(),
    val priority: Int = 100
)