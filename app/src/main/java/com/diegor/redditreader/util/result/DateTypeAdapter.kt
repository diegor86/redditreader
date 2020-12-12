package com.diegor.redditreader.util.result

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.*


class DateTypeAdapter : TypeAdapter<Date>() {

    override fun write(out: JsonWriter, value: Date?) {
        if (value != null) out.value(value.time / 1000L) else out.nullValue()
    }

    override fun read(input: JsonReader): Date {
        return Date(input.nextLong() * 1000L)
    }
}