package com.diegor.redditreader.ui.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
/**
 *  Not doing much ATM. We add this as a starting point. This is were would put all our detail related stuff.
 */
class EntryDetailViewModel @ViewModelInject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _entry = MutableLiveData<Entry>()
    val entry: LiveData<Entry>
        get() = _entry

    fun setEntry(entry: Entry) = viewModelScope.launch(dispatcherProvider.computation) {
        withContext(dispatcherProvider.main) {
            _entry.value = entry
        }
    }
}