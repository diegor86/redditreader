package com.diegor.redditreader.data.entities

import com.google.gson.annotations.SerializedName

class PageData(
    @SerializedName("children") val children: List<EntryData>
)