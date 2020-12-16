package com.diegor.redditreader.data.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Entry(
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("num_comments") val comments: Int,
    @SerializedName("name") val name: String,
    @SerializedName("created") val created: Date,
    @SerializedName("url") val url: String?,
    @Transient val markedAsRead: Boolean = false
) : Parcelable