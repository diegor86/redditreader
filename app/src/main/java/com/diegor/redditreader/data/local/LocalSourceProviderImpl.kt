package com.diegor.redditreader.data.local

import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.data.entities.PageData

class LocalSourceProviderImpl : LocalSourceProvider {
    private var entries = mutableListOf<Entry>()
    private var after: String? = null
    private var before: String? = null

    override fun hasEntries() = entries.isNotEmpty()

    override fun storeAfterEntries(page: PageData) {
        page.children.map { it.entry }.also {
            entries.addAll(it)
        }

        after = entries.lastOrNull()?.name
    }

    override fun storeInitialEntries(page: PageData) {
        page.children.map { it.entry }.also {
            entries.addAll(it)
        }

        after = entries.lastOrNull()?.name
        before = entries.firstOrNull()?.name
    }

    override fun storeBeforeEntries(page: PageData) {
        page.children.map { it.entry }.also {
            entries.addAll(0, it)
        }

        before = entries.firstOrNull()?.name
    }

    override fun getEntries(): List<Entry> = entries.toList()

    override fun getAfter() = after

    override fun getBefore() = before

    override fun markEntryAsRead(entry: Entry) {
        val index = entries.indexOfFirst { it.name == entry.name }

        index.let { entries.set(it, entry.copy(markedAsRead = true)) }
    }

    override fun dismissAllEntries() = entries.clear()

    override fun dismissEntry(entry: Entry) {
        entries.remove(entry)
    }
}