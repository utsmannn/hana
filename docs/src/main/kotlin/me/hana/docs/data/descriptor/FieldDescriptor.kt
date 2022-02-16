package me.hana.docs.data.descriptor

import me.hana.docs.*
import me.hana.docs.annotation.DocDescription
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

data class FieldDescriptor(
    internal var title: String = "",
    internal var dataOrigin: Any = "",
    internal var member: List<MemberFieldDescriptor> = emptyList(),
    internal var objectIncluded: List<MemberFieldDescriptor> = emptyList(),
    internal var jsonString: String = "",
) {

    companion object {
        fun of(data: Any): FieldDescriptor {
            val type = data.javaClass.kotlin

            val title = data::class.simpleName.orEmpty()
            val jsonString = data.toJsonString()

            val member = MemberFieldDescriptor.of(type)
            val extendMember = member.map {
                it.saveKClassObject.map { kClass ->
                    MemberFieldDescriptor.of(kClass)
                }
            }.flatten().flatten()

            val allMember = extendMember + member
            return FieldDescriptor(
                title = title,
                jsonString = jsonString,
                dataOrigin = data,
                member = member,
                objectIncluded = allMember
            )
        }
    }

    data class MemberFieldDescriptor(
        internal var objectId: String = "",
        internal var name: String = "",
        internal var type: String = "",
        internal var idOfType: String = "",
        internal var isRequired: Boolean = false,
        internal var description: String = "",
        internal var objectIncluded: List<String> = listOf(),
    ) {
        val saveKClassObject: MutableList<KClass<out Any>> = mutableListOf()

        companion object {
            fun <T : Any> of(data: KClass<out T>): List<MemberFieldDescriptor> {
                val prop = data.declaredMemberProperties
                val objectIncluded: MutableList<String> = mutableListOf()
                val objectId = data.simpleName.orEmpty()
                val saveKClassObject: MutableList<KClass<out Any>> = mutableListOf()

                return prop.map {
                    val annotation = it.javaField?.getAnnotation(DocDescription::class.java)
                    val desc = annotation?.description.orEmpty()
                    val isRequired = annotation?.isRequired.orFalse()

                    val name = it.name
                    val returnTypeRaw = it.returnType.toString()
                    println("return type found -> $returnTypeRaw")
                    val returnTypeWithId = if (
                        returnTypeRaw.startsWith("kotlin.Array")
                        || returnTypeRaw.startsWith("kotlin.collections.List")
                        || returnTypeRaw.startsWith("kotlin.collections.ArrayList")
                    ) {
                        val originType = it.returnType.arguments.firstOrNull()?.type.toString()
                        val type = it.returnType.arguments.firstOrNull()?.type
                            ?.toString()
                            ?.removeKotlinPackage()
                            ?.lastOfPackage()
                            .orEmpty()

                        val instance = Class.forName(originType).kotlin
                        saveKClassObject.add(instance)
                        if (!type.contains("kotlin.")) {
                            objectIncluded.add(type)
                            TypeWithId("[${type}]", type.idTypeOf())
                        } else {
                            TypeWithId("[${type}]")
                        }
                    } else {
                        val type = returnTypeRaw.removeKotlinPackage().lastOfPackage()
                        if (!returnTypeRaw.contains("kotlin.")) {
                            val clazzOf = Class.forName(returnTypeRaw.innerClassFixed()).kotlin
                            saveKClassObject.add(clazzOf)
                            objectIncluded.add(returnTypeRaw.removeKotlinPackage().lastOfPackage())

                            TypeWithId(type, type.idTypeOf())
                        } else {
                            TypeWithId(type)
                        }

                    }

                    MemberFieldDescriptor(
                        objectId = objectId,
                        name = name,
                        type = returnTypeWithId.type,
                        idOfType = returnTypeWithId.id,
                        isRequired = isRequired,
                        description = desc,
                        objectIncluded = objectIncluded
                    ).apply {
                        this.saveKClassObject.addAll(saveKClassObject)
                    }
                }
            }

            private fun String.innerClassFixed(): String {
                val listPart = split(".")
                val size = listPart.lastIndex
                return if (listPart[size-1].contains("([A-Z])".toRegex())) {
                    replaceAfter(listPart[size-1], "\$${listPart[size]}")
                } else {
                    this
                }
            }

            private data class TypeWithId(val type: String, val id: String = "")
        }
    }
}