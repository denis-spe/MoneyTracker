// Glory be the LORD GOD
package com.example.moneytracker.helper

import com.google.firebase.Timestamp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toFirestoreTimestampUtc(): Timestamp {
    val instant = toInstant(TimeZone.UTC)
    return Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
}

@OptIn(ExperimentalTime::class)
fun Timestamp.toLocalDateTimeUtc(): LocalDateTime {
    val instant = Instant.fromEpochSeconds(seconds, nanoseconds)
    return instant.toLocalDateTime(TimeZone.UTC)
}


