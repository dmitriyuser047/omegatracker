package com.example.omegatracker.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import kotlin.time.Duration


class ObjectJsonAdapter : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation?>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (List::class.java.isAssignableFrom(rawType)
            || Set::class.java.isAssignableFrom(rawType)
            || rawType.isArray
            || Duration::class.java.isAssignableFrom(rawType)
        ) {
            return null
        }

        val delegate = moshi.nextAdapter<Any>(this, type, annotations)
        return object : JsonAdapter<Any?>() {
            @Throws(java.io.IOException::class)
            override fun fromJson(reader: JsonReader): Any? {
                if (reader.peek() != JsonReader.Token.BEGIN_ARRAY) {
                    return delegate.fromJson(reader)
                }

                reader.beginArray()
                reader.endArray()
                return null
            }

            @Throws(java.io.IOException::class)
            override fun toJson(writer: JsonWriter, value: Any?) {
                if (value is Duration) {
                    writer.value(value.toString())
                } else {
                    delegate.toJson(writer, value)
                }
            }
        }
    }

}
