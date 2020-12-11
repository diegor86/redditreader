package com.diegor.redditreader.data.api

import com.diegor.redditreader.data.entities.Page
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditService {
    @GET("/r/all/top")
    suspend fun getTopEntries(@Query("Authorization") authorization: String = AUTHORIZATION,
                              @Query("limit") limit: Int = 25,
                              @Query("show") show: String = "all"
    ): Response<Page>

    companion object {
        // TODO: Hardcoded ATM
        private const val AUTHORIZATION = "bearer -16xa6X2c5vnQv6b4EF9rIp6PczqmTw"
    }
}