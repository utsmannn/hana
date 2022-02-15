package me.hana.docs.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class DocDescription(val description: String, val isRequired: Boolean = false)