package com.diegor.redditreader.domain

import com.diegor.redditreader.data.SimpleStorageRepository
import java.util.*
import javax.inject.Inject

class GetDeviceIdUseCase @Inject constructor(
    private val repository: SimpleStorageRepository
) {

    suspend operator fun invoke(): String {
        var uuid = repository.getString(DEVICE_ID_KEY)

        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString()

            repository.storeValue(DEVICE_ID_KEY, uuid)
        }

        return uuid
    }

    companion object {
        private const val DEVICE_ID_KEY = "device_id_key"
    }
}