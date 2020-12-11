package com.diegor.redditreader.data.api

import com.diegor.redditreader.data.entities.Page
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditService {
    @GET("top")
    suspend fun getTopEntries(@Query("limit") limit: Int = 25,
                              @Query("show") show: String = "all"
    ): Response<Page>
}