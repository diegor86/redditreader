package com.diegor.redditreader.data.local

import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.data.entities.PageData

class LocalSourceProviderImpl : LocalSourceProvider {
    private var entries = mutableListOf<Entry>()

    override fun hasEntries() = entries.isNotEmpty()

    override fun storeAfterEntries(page: PageData) {
        page.children.map { it.entry }.also {
            entries.addAll(it)
        }
    }

    override fun storeInitialEntries(page: PageData) {
        page.children.map { it.entry }.also {
            entries.addAll(it)
        }
    }

    override fun storeBeforeEntries(page: PageData) {
        page.children.map { it.entry }.also {
            entries.addAll(0, it)
        }
    }

    override fun getEntries(): List<Entry> = entries.toList()

    override fun getAfter() = entries.lastOrNull()?.name

    override fun getBefore() = entries.firstOrNull()?.name

    override fun markEntryAsRead(entry: Entry) {
        val index = entries.indexOfFirst { it.name == entry.name }

        index.let { entries.set(it, entry.copy(markedAsRead = true)) }
    }
}