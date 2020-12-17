package com.diegor.redditreader.domain

import com.diegor.redditreader.data.SimpleStorageRepository
import com.diegor.redditreader.util.DeviceIdGenerator
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

@ExperimentalCoroutinesApi
class GetDeviceIdUseCaseTest {

    lateinit var getDeviceIdUseCase: GetDeviceIdUseCase
    lateinit var repository: SimpleStorageRepository
    lateinit var deviceIdGenerator: DeviceIdGenerator

    @Before
    fun before() {
        repository = mock()
        deviceIdGenerator = mock()

        getDeviceIdUseCase = GetDeviceIdUseCase(repository, deviceIdGenerator)
    }

    @Test
    fun `Test retrieve device Id when stored`() = runBlockingTest {
        val id = "some stored device id"
        repository.stub {
            onBlocking { getString(anyString(), anyString()) }.doReturn(id)
        }

        Assert.assertEquals(id, getDeviceIdUseCase())
    }

    @Test
    fun `Test repository being called when getting the device id`() = runBlockingTest {
        val id = "some stored device id"
        repository.stub {
            onBlocking { getString(anyString(), anyString()) }.doReturn(id)
        }

        getDeviceIdUseCase()

        verify(repository).getString(anyString(), anyString())
    }

    @Test
    fun `Test id generator not being called when device id stored`() = runBlockingTest {
        val id = "some stored device id"
        repository.stub {
            onBlocking { getString(anyString(), anyString()) }.doReturn(id)
        }

        getDeviceIdUseCase()

        verify(deviceIdGenerator, never()).generateDeviceId()
    }

    @Test
    fun `Test device id generated when not stored`() = runBlockingTest {
        val emptyId = ""
        repository.stub {
            onBlocking { getString(anyString(), anyString()) }.doReturn(emptyId)
        }

        val id = "some generated id"
        whenever(deviceIdGenerator.generateDeviceId()).thenReturn(id)

        Assert.assertEquals(id, getDeviceIdUseCase())
    }

    @Test
    fun `Test device id stored when generated`() = runBlockingTest {
        val emptyId = ""
        repository.stub {
            onBlocking { getString(anyString(), anyString()) }.doReturn(emptyId)
        }

        val id = "some generated id"
        whenever(deviceIdGenerator.generateDeviceId()).thenReturn(id)

        getDeviceIdUseCase()

        verify(repository).storeValue(anyString(), eq(id))
    }
}