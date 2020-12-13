package com.diegor.redditreader.data.local

import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.data.entities.PageData

interface LocalSourceProvider {
    fun storeAfterEntries(page: PageData)
    fun getEntries(): List<Entry>
    fun getAfter(): String?
    fun storeBeforeEntries(page: PageData)
    fun getBefore(): String?
    fun markEntryAsRead(entry: Entry)
    fun storeInitialEntries(page: PageData)
    fun hasEntries(): Boolean
}