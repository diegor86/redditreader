package com.diegor.redditreader.data.api

import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

class RedditRepository @Inject constructor(private val service: RedditService
) {

    suspend fun getTopEntries(): Flow<Result<List<Entry>>> = flow {
        emit(Result.Loading)

        val response = service.getTopEntries()

        if (response.isSuccessful) {
            emit(Result.Success(response.body()!!.data.children))
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(parseErrors(it))))
            }
        }
    }

    private fun parseErrors(errorBody: ResponseBody): String {
        // TODO parse error

        return "TODO"
    }

}