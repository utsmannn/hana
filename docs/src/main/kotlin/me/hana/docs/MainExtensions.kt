package me.hana.docs

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.html.HTMLTag
import kotlinx.html.unsafe
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.util.*

fun <T>T.toJsonString(beauty: Boolean = true): String {
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

fun Boolean?.orFalse(): Boolean = this ?: false

fun String.lastOfPackage(): String {
    return split(".").last()
}

fun String.removeKotlinPackage(): String {
    return replace("kotlin.", "")
}

fun String.removeStringTag(): String {
    return replace("\"", "")
}

fun String.replaceSpaceUrl(): String {
    return replace(" ", "+")
}
fun String.removeNullString(): String {
    return replace("null", "")
}

fun String.idTypeOf(): String {
    return lowercase().replace(" ", "-")
}

fun HTMLTag.markdown(markdown: String, inline: Boolean = false) {
    val parser = Parser.builder().build()
    val renderer = HtmlRenderer.builder().build()

    val html = renderer.render(parser.parse(markdown))

    unsafe {
        if (inline)
            +html.replace(Regex("</?p>"), "")
        else +html
    }
}