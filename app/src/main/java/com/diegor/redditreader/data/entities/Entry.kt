package com.diegor.redditreader.data.entities

import com.google.gson.annotations.SerializedName

class Entry(@SerializedName("title") private val title: String,
            @SerializedName("author") private val author: String,
            @SerializedName("thumbnail") private val thumbnail: String?,
            @SerializedName("num_comments") private val comments: Int
)