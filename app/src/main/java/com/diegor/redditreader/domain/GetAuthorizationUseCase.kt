package com.diegor.redditreader.domain

import com.diegor.redditreader.data.RedditRepository
import com.diegor.redditreader.data.api.AuthorizationHolder
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAuthorizationUseCase @Inject constructor(
    private val authorizationHolder: AuthorizationHolder,
    private val repository: RedditRepository
) {

    suspend operator fun invoke(): Flow<Result<String>> = flow {

        authorizationHolder.authorization?.let {
            emit(Result.Success(it))
        } ?: run {
            emit(Result.Loading)
            val result = repository.getAuthorization(DEVICE_ID)

            if (result is Result.Success) {
                authorizationHolder.authorization = result.data
            }

            emit(result)
        }
    }

    companion object {
        // TODO hardcoded ATM
        private const val DEVICE_ID = "9537d868-b682-4afe-a0d2-ec83453e0bce"
    }
}