package com.diegor.redditreader.data.entities

import com.google.gson.annotations.SerializedName

data class Entry(@SerializedName("title") val title: String,
            @SerializedName("author") val author: String,
            @SerializedName("thumbnail") val thumbnail: String?,
            @SerializedName("num_comments") val comments: Int,
            @SerializedName("name") val name: String
)