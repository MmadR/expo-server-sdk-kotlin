package io.expo.sdk

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import io.expo.sdk.model.ErrorDetail
import io.expo.sdk.model.Priority
import io.expo.sdk.model.ResponseStatus
import java.io.InputStream

class KlaxonJsonSerializer {

    var klaxon = Klaxon()

    init {
        klaxon.converter(ErrorDetailConverter())
        klaxon.converter(ResponseStatusConverter())
        klaxon.converter(PriorityConverter())
    }

    fun toJson(obj: Any): String =
            klaxon.toJsonString(obj)

    inline fun <reified T> fromJson(json: InputStream): T? =
        klaxon.parse<T>(json)
}

class ErrorDetailConverter: Converter{
    override fun canConvert(cls: Class<*>): Boolean =
            ErrorDetail::class.java.isAssignableFrom(cls)

    override fun fromJson(jv: JsonValue): Any =
            ErrorDetail.values().find { it.code == jv.inside}?:ErrorDetail.UNKNOWN

    override fun toJson(value: Any): String =
            (value as ErrorDetail).code
}

class ResponseStatusConverter: Converter{
    override fun canConvert(cls: Class<*>): Boolean =
            ResponseStatus::class.java.isAssignableFrom(cls)

    override fun fromJson(jv: JsonValue): Any =
            if(ResponseStatus.OK.status == jv.inside)ResponseStatus.OK else ResponseStatus.ERROR

    override fun toJson(value: Any): String =
            (value as ResponseStatus).status
}

class PriorityConverter: Converter{
    override fun canConvert(cls: Class<*>): Boolean =
            Priority::class.java.isAssignableFrom(cls)

    override fun fromJson(jv: JsonValue): Any =
            Priority.values().find { it.value == jv.inside}?:Priority.DEFAULT

    override fun toJson(value: Any): String =
            (value as Priority).value
}