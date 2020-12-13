package com.diegor.redditreader.domain

import com.diegor.redditreader.data.RedditRepository
import com.diegor.redditreader.data.entities.Entry
import javax.inject.Inject

class MarkEntryAsReadUseCase @Inject constructor(
    private val repository: RedditRepository
) {

    suspend operator fun invoke(entry: Entry) = repository.markEntryAsRead(entry)
}