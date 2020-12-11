package com.diegor.redditreader.data.entities

import com.google.gson.annotations.SerializedName

class Page(@SerializedName("data") val data: PageData) {
}