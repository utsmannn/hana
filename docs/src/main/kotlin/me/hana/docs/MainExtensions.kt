package me.hana.docs

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.html.HTMLTag
import kotlinx.html.unsafe
import me.hana.docs.data.DocFile
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.util.*
import kotlin.reflect.KClass

internal fun <T>T.toJsonString(beauty: Boolean = true): String {
    val gson = GsonBuilder()
        .apply {
            if (beauty) {
                setPrettyPrinting()
            }
        }
        .create()
    val type = object : TypeToken<T>() {}.type
    return gson.toJson(this, type)
}

internal fun Boolean?.orFalse(): Boolean = this ?: false

internal fun String.lastOfPackage(): String {
    return split(".").last()
}

internal fun String.removeKotlinPackage(): String {
    return replace("kotlin.", "")
}

internal fun String.removeStringTag(): String {
    return replace("\"", "")
}

internal fun String.replaceSpaceUrl(): String {
    return replace(" ", "+")
}
internal fun String.removeNullString(): String {
    return replace("null", "")
}

internal fun String.idTypeOf(): String {
    return lowercase().replace(" ", "-")
}

internal fun HTMLTag.markdown(markdown: String, inline: Boolean = false) {
    val parser = Parser.builder().build()
    val renderer = HtmlRenderer.builder().build()

    val html = renderer.render(parser.parse(markdown))

    unsafe {
        if (inline)
            +html.replace(Regex("</?p>"), "")
        else +html
    }
}

internal fun <T: Any>KClass<T>.isDocFile(): Boolean {
    return isInstance(DocFile("")).orFalse()
}

fun String.innerClassFixed(): String {
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