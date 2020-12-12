package com.diegor.redditreader.data

import com.diegor.redditreader.data.api.AuthorizationService
import com.diegor.redditreader.data.api.RedditService
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

class RedditRepository @Inject constructor(
    private val redditService: RedditService,
    private val authorizationService: AuthorizationService
) {

    suspend fun getTopEntries(): Flow<Result<List<Entry>>> = flow {
        emit(Result.Loading)

        val response = redditService.getTopEntries()

        if (response.isSuccessful) {
            emit(Result.Success(response.body()!!.data.children.map { it.entry }))
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(parseErrors(it))))
            }
        }
    }

    suspend fun getAuthorization(deviceId: String): Result<String> {

        val response = authorizationService.getAuthorization(deviceId)

        if (response.isSuccessful) {
            return Result.Success(response.body()!!.accessToken)
        } else {
            response.errorBody()?.let {
                return Result.Error(Exception(parseErrors(it)))
            } ?: run {
                return Result.Error(Exception("Unknown error"))
            }
        }
    }

    private fun parseErrors(errorBody: ResponseBody): String {
        // TODO parse error

        return "TODO"
    }

}