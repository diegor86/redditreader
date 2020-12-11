package com.diegor.redditreader.ui.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.domain.GetTopEntriesUseCase
import com.diegor.redditreader.util.result.Event
import com.diegor.redditreader.util.result.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryListViewModel @ViewModelInject constructor(
    private val getTopEntriesUseCase: GetTopEntriesUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _entryList = MutableLiveData<List<Entry>>()
    val entryList: LiveData<List<Entry>>
        get() = _entryList

    private val _errors = MutableLiveData<Event<String>>()
    val errors: LiveData<Event<String>>
        get() = _errors

    fun getTopEntries() = viewModelScope.launch(Dispatchers.Default)  {
        getTopEntriesUseCase().collect { result ->
            when (result) {
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        _entryList.value = result.data
                    }
                }
                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        _errors.value = Event(result.exception.toString())
                    }
                }
            }
        }
    }
}