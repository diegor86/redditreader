package com.diegor.redditreader.util.result

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceIdGenerator @Inject constructor() {

    fun generateDeviceId() = UUID.randomUUID().toString()

}