package com.diegor.redditreader.data.entities

import com.google.gson.annotations.SerializedName

class PageData(@SerializedName("children") private val children: List<Entry>)