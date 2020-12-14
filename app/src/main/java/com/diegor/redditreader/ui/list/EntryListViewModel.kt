package com.diegor.redditreader.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.domain.*
import com.diegor.redditreader.util.result.Event
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers
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

    fun authenticateAndGetEntries() = viewModelScope.launch(Dispatchers.Default) {
        if (_entryList.value != null) return@launch

        getAuthorizationUseCase().collect { result ->
            when (result) {
                is Result.Loading -> {
                    withContext(Dispatchers.Main) {
                        _loading.value = true
                    }
                }
                is Result.Success -> {
                    getStartingEntries()
                }
                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        _loading.value = false
                        _errors.value = Event(result.exception.toString())
                    }
                }
            }
        }
    }

    private fun getStartingEntries() = viewModelScope.launch(Dispatchers.Default)  {
        getInitialEntriesUseCase().collect { result ->
            showEntriesResult(result)
        }
    }

    private suspend fun showEntriesResult(result: Result<List<Entry>>) {
        when (result) {
            is Result.Loading -> {
                withContext(Dispatchers.Main) {
                    _loading.value = true
                }
            }
            is Result.Success -> {
                withContext(Dispatchers.Main) {
                    _loading.value = false
                    _entryList.value = result.data
                }
            }
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    _loading.value = false
                    _errors.value = Event(result.exception.toString())
                }
            }
        }
    }

    fun getOlderEntries() = viewModelScope.launch(Dispatchers.Default)  {
        if (loading.value == true) return@launch

        getOlderEntriesUseCase().collect { result ->
            showEntriesResult(result)
        }
    }

    fun refreshNewEntries() = viewModelScope.launch(Dispatchers.Default)  {
        if (loading.value == true) return@launch

        refreshNewEntriesUseCase().collect { result ->
            showEntriesResult(result)
        }
    }

    fun onEntryTapped(entry: Entry) = viewModelScope.launch(Dispatchers.Default) {
        val list = markEntryAsReadUseCase(entry)

        withContext(Dispatchers.Main) {
            _entryList.value = list
            _showDetail.value = Event(entry)
        }
    }

    fun dismissAllEntries() = viewModelScope.launch(Dispatchers.Default) {
        val list = dismissAllEntriesUseCase()

        withContext(Dispatchers.Main) {
            _entryList.value = list
        }
    }

    fun dismissEntry(entry: Entry) = viewModelScope.launch(Dispatchers.Default) {
        val list = dismissEntryUseCase(entry)

        withContext(Dispatchers.Main) {
            _entryList.value = list
        }
    }
}