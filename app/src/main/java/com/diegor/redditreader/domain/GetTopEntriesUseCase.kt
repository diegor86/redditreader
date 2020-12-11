package com.diegor.redditreader.domain

import com.diegor.redditreader.data.api.RedditRepository
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopEntriesUseCase @Inject constructor(
    private val repository: RedditRepository
) {

    suspend operator fun invoke(): Flow<Result<List<Entry>>> {
        return repository.getTopEntries()
    }

}