package com.diegor.redditreader.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.domain.*
import com.diegor.redditreader.util.*
import com.diegor.redditreader.util.result.Result
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class EntryListViewModelTest {

    private lateinit var getInitialEntriesUseCase: GetInitialEntriesUseCase
    private lateinit var getOlderEntriesUseCase: GetOlderEntriesUseCase
    private lateinit var refreshNewEntriesUseCase: RefreshNewEntriesUseCase
    private lateinit var getAuthorizationUseCase: GetAuthorizationUseCase
    private lateinit var markEntryAsReadUseCase: MarkEntryAsReadUseCase
    private lateinit var dismissAllEntriesUseCase: DismissAllEntriesUseCase
    private lateinit var dismissEntryUseCase: DismissEntryUseCase

    private lateinit var viewModel: EntryListViewModel

    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        getInitialEntriesUseCase = mock()
        getOlderEntriesUseCase = mock()
        refreshNewEntriesUseCase = mock()
        getAuthorizationUseCase = mock()
        markEntryAsReadUseCase = mock()
        dismissAllEntriesUseCase = mock()
        dismissEntryUseCase = mock()
        val savedStateHandle = mock<SavedStateHandle>()

        viewModel = EntryListViewModel(
            getInitialEntriesUseCase,
            getOlderEntriesUseCase,
            refreshNewEntriesUseCase,
            getAuthorizationUseCase,
            markEntryAsReadUseCase,
            dismissAllEntriesUseCase,
            dismissEntryUseCase,
            provideFakeCoroutinesDispatcherProvider(coroutinesRule.testDispatcher),
            savedStateHandle
        )
    }

    @Test
    fun `Test get older reddit entries`() = runBlockingTest  {
        val mockedEntries = mockOlderEntries()

        whenever(getOlderEntriesUseCase.invoke()) doReturn flow {
            emit(Result.Loading)
            emit(Result.Success(mockedEntries))
        }

        viewModel.getOlderEntries()
        val uiState = viewModel.entryList.getOrAwaitValue()

        Assert.assertEquals(mockedEntries, uiState)
    }

    @Test
    fun `Test loading emitted when we start to get older entries`() = runBlockingTest  {
        whenever(getOlderEntriesUseCase.invoke()) doReturn flow {
            emit(Result.Loading)
        }

        // Check that wen we retrieve the entries
        viewModel.getOlderEntries()
        val loading = viewModel.loading.getOrAwaitValue()

        Assert.assertEquals(true, loading)
    }

    @Test
    fun `Test loading dismissed when we end getting older entries`() = runBlockingTest  {
        whenever(getOlderEntriesUseCase.invoke()) doReturn flow {
            emit(Result.Success(mockOlderEntries()))
        }

        // Check that wen we retrieve the entries
        viewModel.getOlderEntries()
        val loading = viewModel.loading.getOrAwaitValue()

        Assert.assertEquals(false, loading)
    }

    private fun mockOlderEntries() = listOf(
        Entry(
            "title 1",
            "author 1",
            "thumbnail 1",
            23,
            "name 1",
            Date(257),
            "url 1"
        ),
        Entry(
            "title 2",
            "author 2",
            "thumbnail 2",
            87,
            "name 2",
            Date(346),
            "url 2"
        )
    )
}