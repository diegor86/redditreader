package com.diegor.redditreader.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.domain.*
import com.diegor.redditreader.util.CoroutinesDispatcherProvider
import com.diegor.redditreader.util.result.Event
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryListViewModel @ViewModelInject constructor(
    private val getInitialEntriesUseCase: GetInitialEntriesUseCase,
    private val getOlderEntriesUseCase: GetOlderEntriesUseCase,
    private val refreshNewEntriesUseCase: RefreshNewEntriesUseCase,
    private val getAuthorizationUseCase: GetAuthorizationUseCase,
    private val markEntryAsReadUseCase: MarkEntryAsReadUseCase,
    private val dismissAllEntriesUseCase: DismissAllEntriesUseCase,
    private val dismissEntryUseCase: DismissEntryUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _entryList = MutableLiveData<List<Entry>>()
    val entryList: LiveData<List<Entry>>
        get() = _entryList

    private val _errors = MutableLiveData<Event<String>>()
    val errors: LiveData<Event<String>>
        get() = _errors

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _showDetail = MutableLiveData<Event<Entry>>()
    val showDetail: LiveData<Event<Entry>>
        get() = _showDetail

    private val _openUrl = MutableLiveData<Event<String>>()
    val openUrl: LiveData<Event<String>>
        get() = _openUrl

    fun authenticateAndGetEntries() = viewModelScope.launch(dispatcherProvider.computation) {
        if (_entryList.value != null) return@launch

        getAuthorizationUseCase().collect { result ->
            when (result) {
                is Result.Loading -> {
                    withContext(dispatcherProvider.main) {
                        _loading.value = true
                    }
                }
                is Result.Success -> {
                    getStartingEntries()
                }
                is Result.Error -> {
                    withContext(dispatcherProvider.main) {
                        _loading.value = false
                        _errors.value = Event(result.exception.toString())
                    }
                }
            }
        }
    }

    private fun getStartingEntries() = viewModelScope.launch(dispatcherProvider.computation)  {
        getInitialEntriesUseCase().collect { result ->
            showEntriesResult(result)
        }
    }

    private suspend fun showEntriesResult(result: Result<List<Entry>>) {
        when (result) {
            is Result.Loading -> {
                withContext(dispatcherProvider.main) {
                    _loading.value = true
                }
            }
            is Result.Success -> {
                withContext(dispatcherProvider.main) {
                    _loading.value = false
                    _entryList.value = result.data
                }
            }
            is Result.Error -> {
                withContext(dispatcherProvider.main) {
                    _loading.value = false
                    _errors.value = Event(result.exception.toString())
                }
            }
        }
    }

    fun getOlderEntries() = viewModelScope.launch(dispatcherProvider.computation)  {
        if (loading.value == true) return@launch

        getOlderEntriesUseCase().collect { result ->
            showEntriesResult(result)
        }
    }

    fun refreshNewEntries() = viewModelScope.launch(dispatcherProvider.computation)  {
        if (loading.value == true) return@launch

        refreshNewEntriesUseCase().collect { result ->
            showEntriesResult(result)
        }
    }

    fun onEntryTapped(entry: Entry) = viewModelScope.launch(dispatcherProvider.computation) {
        val list = markEntryAsReadUseCase(entry)

        withContext(dispatcherProvider.main) {
            _entryList.value = list
            _showDetail.value = Event(entry)
        }
    }

    fun dismissAllEntries() = viewModelScope.launch(dispatcherProvider.computation) {
        val list = dismissAllEntriesUseCase()

        withContext(dispatcherProvider.main) {
            _entryList.value = list
        }
    }

    fun dismissEntry(entry: Entry) = viewModelScope.launch(dispatcherProvider.computation) {
        val list = dismissEntryUseCase(entry)

        withContext(dispatcherProvider.main) {
            _entryList.value = list
        }
    }

    fun onThumbnailTapped(entry: Entry) {
        entry.url?.let {
            _openUrl.value = Event(it)
        }
    }
}