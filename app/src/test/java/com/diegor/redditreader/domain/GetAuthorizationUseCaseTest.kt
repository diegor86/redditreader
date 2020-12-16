package com.diegor.redditreader.domain

import com.diegor.redditreader.data.RedditRepository
import com.diegor.redditreader.data.api.AuthorizationHolder
import com.diegor.redditreader.util.result.Result
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAuthorizationUseCaseTest {

    lateinit var getAuthorizationUseCase: GetAuthorizationUseCase
    lateinit var authorizationHolder: AuthorizationHolder
    lateinit var getDeviceIdUseCase: GetDeviceIdUseCase
    lateinit var repository: RedditRepository

    @Before
    fun before() {
        authorizationHolder = mock()
        getDeviceIdUseCase = mock()
        repository = mock()

        getAuthorizationUseCase = GetAuthorizationUseCase(authorizationHolder, getDeviceIdUseCase, repository)
    }

    @Test
    fun `Test retrieve a stored authorization`() = runBlockingTest {
        val authorization = "some stored authorization"
        whenever(authorizationHolder.authorization).thenReturn(authorization)

        val expected = listOf(Result.Success(authorization))

        Assert.assertEquals(expected, getAuthorizationUseCase().toList())
    }

    @Test
    fun `Test authorization holder called`() = runBlockingTest {
        val authorization = "some stored authorization"
        whenever(authorizationHolder.authorization).thenReturn(authorization)

        getAuthorizationUseCase().toList()

        verify(authorizationHolder).authorization
    }

    @Test
    fun `Test fetch authorization when not stored`() = runBlockingTest {
        whenever(authorizationHolder.authorization).thenReturn(null)

        val deviceId = "some device id"

        getDeviceIdUseCase.stub {
            onBlocking { invoke() }.doReturn(deviceId)
        }

        val authorization = "some authorization"
        repository.stub {
            onBlocking { getAuthorization(deviceId) }.doReturn(Result.Success(authorization))
        }

        val expected = listOf(Result.Loading, Result.Success(authorization))

        Assert.assertEquals(expected, getAuthorizationUseCase().toList())
    }

    @Test
    fun `Test authorization being stored after retrieving it`() = runBlockingTest {
        whenever(authorizationHolder.authorization).thenReturn(null)

        val deviceId = "some device id"

        getDeviceIdUseCase.stub {
            onBlocking { invoke() }.doReturn(deviceId)
        }

        val authorization = "some authorization"
        repository.stub {
            onBlocking { getAuthorization(deviceId) }.doReturn(Result.Success(authorization))
        }

        getAuthorizationUseCase().toList()

        verify(authorizationHolder).authorization = authorization
    }

    @Test
    fun `Test error when fetching authorization`() = runBlockingTest {
        whenever(authorizationHolder.authorization).thenReturn(null)

        val deviceId = "some device id"

        getDeviceIdUseCase.stub {
            onBlocking { invoke() }.doReturn(deviceId)
        }

        val error = Result.Error()
        repository.stub {
            onBlocking { getAuthorization(deviceId) }.doReturn(error)
        }

        getAuthorizationUseCase().toList()

        val expected = listOf(Result.Loading, error)

        Assert.assertEquals(expected, getAuthorizationUseCase().toList())
    }

    @Test
    fun `Test authorization not being stored after an error`() = runBlockingTest {
        whenever(authorizationHolder.authorization).thenReturn(null)

        val deviceId = "some device id"

        getDeviceIdUseCase.stub {
            onBlocking { invoke() }.doReturn(deviceId)
        }

        val error = Result.Error()
        repository.stub {
            onBlocking { getAuthorization(deviceId) }.doReturn(error)
        }

        getAuthorizationUseCase().toList()

        verify(authorizationHolder, never()).authorization = anyOrNull()
    }
}