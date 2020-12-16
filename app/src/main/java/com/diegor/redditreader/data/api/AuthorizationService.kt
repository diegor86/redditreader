package com.diegor.redditreader.data.api

import com.diegor.redditreader.data.entities.AuthorizationResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthorizationService {

    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun getAuthorization(
        @Field("device_id") deviceId: String,
        @Field("grant_type") grantType: String = GRANT_TYPE
    ): Response<AuthorizationResponse>

    companion object {
        const val REDDIT_APP_ID = "6VK697jnFSsg6g"
        const val AUTH_BASE_URL = "https://www.reddit.com"
        private const val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
    }
}