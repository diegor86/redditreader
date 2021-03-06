package com.diegor.redditreader.data

import com.diegor.redditreader.data.api.AuthorizationService
import com.diegor.redditreader.data.api.RedditService
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.data.local.LocalSourceProvider
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class RedditRepository @Inject constructor(
    private val redditService: RedditService,
    private val localSourceProvider: LocalSourceProvider,
    private val authorizationService: AuthorizationService
) {

    suspend fun getInitialEntries(): Flow<Result<List<Entry>>> = flow {
        if (localSourceProvider.hasEntries()) {
            emit(Result.Success(localSourceProvider.getEntries()))
            return@flow
        }

        emit(Result.Loading)

        val response = redditService.getTopEntries()

        if (response.isSuccessful) {
            response.body()?.data?.let {
                localSourceProvider.storeInitialEntries(it)
            }
            emit(Result.Success(localSourceProvider.getEntries()))
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(parseErrors(response))))
            }
        }
    }

    suspend fun getAfterEntries(): Flow<Result<List<Entry>>> = flow {
        emit(Result.Loading)

        val response = redditService.getTopEntries(after = localSourceProvider.getAfter())

        if (response.isSuccessful) {
            response.body()?.data?.let {
                localSourceProvider.storeAfterEntries(it)
            }
            emit(Result.Success(localSourceProvider.getEntries()))
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(parseErrors(response))))
            }
        }
    }

    suspend fun getBeforeEntries(): Flow<Result<List<Entry>>> = flow {
        emit(Result.Loading)

        val response = redditService.getTopEntries(before = localSourceProvider.getBefore())

        if (response.isSuccessful) {
            response.body()?.data?.let {
                localSourceProvider.storeBeforeEntries(it)
            }
            emit(Result.Success(localSourceProvider.getEntries()))
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(parseErrors(response))))
            }
        }
    }

    suspend fun markEntryAsRead(entry: Entry): List<Entry> {
        localSourceProvider.markEntryAsRead(entry)

        return localSourceProvider.getEntries()
    }

    suspend fun dismissAllEntries(): List<Entry> {
        localSourceProvider.dismissAllEntries()

        return localSourceProvider.getEntries()
    }

    suspend fun dismissEntry(entry: Entry): List<Entry> {
        localSourceProvider.dismissEntry(entry)

        return localSourceProvider.getEntries()
    }

    suspend fun getAuthorization(deviceId: String): Result<String> {

        val response = authorizationService.getAuthorization(deviceId)

        return if (response.isSuccessful) {
            Result.Success(response.body()!!.accessToken)
        } else {
            Result.Error(Exception(parseErrors(response)))
        }
    }

    private fun <T> parseErrors(response: Response<T>): String {
        return "Response error: " + response.code()
    }
}