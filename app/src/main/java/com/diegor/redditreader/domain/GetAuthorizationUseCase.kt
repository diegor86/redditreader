package com.diegor.redditreader.domain

import com.diegor.redditreader.data.RedditRepository
import com.diegor.redditreader.data.api.AuthorizationHolder
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAuthorizationUseCase @Inject constructor(
    private val authorizationHolder: AuthorizationHolder,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val repository: RedditRepository
) {

    suspend operator fun invoke(): Flow<Result<String>> = flow {

        authorizationHolder.authorization?.let {
            emit(Result.Success(it))
        } ?: run {
            emit(Result.Loading)
            val result = repository.getAuthorization(getDeviceIdUseCase())

            if (result is Result.Success) {
                authorizationHolder.authorization = result.data
            }

            emit(result)
        }
    }
}