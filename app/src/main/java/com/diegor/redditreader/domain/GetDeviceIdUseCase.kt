package com.diegor.redditreader.domain

import com.diegor.redditreader.data.SimpleStorageRepository
import com.diegor.redditreader.util.result.DeviceIdGenerator
import javax.inject.Inject

class GetDeviceIdUseCase @Inject constructor(
    private val repository: SimpleStorageRepository,
    private val deviceIdGenerator: DeviceIdGenerator
) {

    suspend operator fun invoke(): String {
        var uuid = repository.getString(DEVICE_ID_KEY)

        if (uuid.isEmpty()) {
            uuid = deviceIdGenerator.generateDeviceId()

            repository.storeValue(DEVICE_ID_KEY, uuid)
        }

        return uuid
    }

    companion object {
        private const val DEVICE_ID_KEY = "device_id_key"
    }
}