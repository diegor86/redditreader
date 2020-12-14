package com.diegor.redditreader.domain

import com.diegor.redditreader.data.RedditRepository
import javax.inject.Inject

class DismissAllEntriesUseCase @Inject constructor(
    private val repository: RedditRepository
) {

    suspend operator fun invoke() = repository.dismissAllEntries()
}