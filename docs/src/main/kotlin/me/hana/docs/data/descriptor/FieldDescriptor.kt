package me.hana.docs.data.descriptor

import me.hana.docs.*
import me.hana.docs.annotation.DocFieldDescription
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
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

            val clazzReturn = MemberFieldDescriptor.of(type)
            val member = clazzReturn.member
            val extendMember = member.map {
                it.saveKClassObject.map { kClass ->
                    MemberFieldDescriptor.of(kClass).member
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
            fun <T : Any> of(data: KClass<out T>): ClazzReturn {
                val prop = data.declaredMemberProperties
                val objectIncluded: MutableList<String> = mutableListOf()
                val objectId = data.simpleName.orEmpty()
                val saveKClassObject: MutableList<KClass<out Any>> = mutableListOf()

                val member = prop.map {
                    val annotation = it.javaField?.getAnnotation(DocFieldDescription::class.java)
                    val desc = annotation?.description.orEmpty()
                    val isRequired = annotation?.isRequired.orFalse()

                    val name = it.name
                    val returnTypeRaw = it.returnType.toString()
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
                            val clazzOf = try {
                                val kClass = Class.forName(returnTypeRaw.innerClassFixed()).kotlin
                                val instance = kClass.createInstance()
                                instance::class.java.kotlin
                            } catch (e: ClassNotFoundException) {
                                throw (ClassNotFoundException("Class of '$returnTypeRaw' not allowed, please cek documentation"))
                            }
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

                return ClazzReturn(
                    type = data.qualifiedName.orEmpty(),
                    member = member
                )
            }

            private fun String.innerClassFixed(): String {
                val listPart = split(".")
                val size = listPart.lastIndex
                return try {
                    if (listPart[size-1].contains("([A-Z])".toRegex())) {
                        replaceAfter(listPart[size-1], "\$${listPart[size]}")
                    } else {
                        this
                    }
                } catch (e: IndexOutOfBoundsException) {
                    this
                }
            }

            private data class TypeWithId(val type: String, val id: String = "")
        }
    }

    data class ClazzReturn(
        var type: String = "",
        var member: List<MemberFieldDescriptor> = emptyList()
    )
}