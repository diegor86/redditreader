package com.diegor.redditreader.data.entities

import com.google.gson.annotations.SerializedName

class AuthorizationResponse(@SerializedName("access_token") val accessToken: String)