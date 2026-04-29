package com.example.moneytracker.helper

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

interface TimeProvider {
    fun nowMillis(): Long = System.currentTimeMillis()
    fun nowZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
        ZonedDateTime.now(zone)

    fun nowLocalDateTime(): LocalDateTime = LocalDateTime.now()
}

object SystemTimeProvider : TimeProvider
