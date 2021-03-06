package com.diegor.redditreader.data.api

import com.diegor.redditreader.data.entities.Page
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditService {
    @GET("top")
    suspend fun getTopEntries(@Query("limit") limit: Int = 25,
                              @Query("show") show: String = "all",
                              @Query("after") after: String? = null,
                              @Query("before") before: String? = null
    ): Response<Page>

    companion object {
        const val REDDIT_BASE_URL = "https://oauth.reddit.com"
    }
}