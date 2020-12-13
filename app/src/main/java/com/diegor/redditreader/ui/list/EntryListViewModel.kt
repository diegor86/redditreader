package com.diegor.redditreader.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.domain.GetAuthorizationUseCase
import com.diegor.redditreader.domain.GetTopEntriesUseCase
import com.diegor.redditreader.util.result.Event
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryListViewModel @ViewModelInject constructor(
    private val getTopEntriesUseCase: GetTopEntriesUseCase,
    private val getAuthorizationUseCase: GetAuthorizationUseCase,
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
        getTopEntriesUseCase().collect { result ->
            when (result) {
                is Result.Loading -> {
                    withContext(Dispatchers.Main) {
                        _loading.value = true
                    }
                }
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        _loading.value = false
                        _entryList.value = result.data.toMutableList()
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
    }

    fun onBottomReached() = viewModelScope.launch(Dispatchers.Default) {
        if (loading.value == true) return@launch

        entryList.value?.last()?.let {
            getTopEntriesUseCase(after = it.name).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        withContext(Dispatchers.Main) {
                            _loading.value = true
                        }
                    }
                    is Result.Success -> {
                        val list = _entryList.value?.toMutableList() ?: mutableListOf()
                        list.addAll(result.data)
                        withContext(Dispatchers.Main) {
                            _loading.value = false
                            _entryList.value = list
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
        }
    }

    fun onRefreshTop() = viewModelScope.launch(Dispatchers.Default) {
        if (loading.value == true) return@launch

        entryList.value?.first()?.let {
            getTopEntriesUseCase(before = it.name).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        withContext(Dispatchers.Main) {
                            _loading.value = true
                        }
                    }
                    is Result.Success -> {
                        val list = _entryList.value?.toMutableList() ?: mutableListOf()
                        list.addAll(0, result.data)
                        withContext(Dispatchers.Main) {
                            _loading.value = false
                            _entryList.value = list
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
        }
    }

    fun onEntryTapped(entry: Entry) = viewModelScope.launch(Dispatchers.Default) {
        val list = _entryList.value?.toMutableList()

        var updated: Entry? = null

        list?.indexOf(entry)?.let {
            updated = entry.copy()
            updated?.markedAsRead = true
            list.set(it, updated!!)
        }

        list?.let {
            withContext(Dispatchers.Main) {
                _entryList.value = it
                updated?.let { _showDetail.value = Event(it) }
            }
        }
    }
}