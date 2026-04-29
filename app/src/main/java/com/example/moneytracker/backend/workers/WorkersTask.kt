package com.example.moneytracker.backend.workers

import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.helper.SystemTimeProvider
import com.example.moneytracker.helper.TimeProvider
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.google.firebase.Timestamp
import kotlinx.datetime.toJavaLocalDateTime
import java.time.Duration

data class WorkersTask(
    val userId: String,
    val datasetId: String,
    val deadlineDateTime: Timestamp,
    val routineData: RoutineData,
    val timeProvider: TimeProvider = SystemTimeProvider
) {

    val calculateDelay: Long
        get() {
            val now = timeProvider.nowZonedDateTime()
            val zone = now.zone
            val target = deadlineDateTime.toLocalDateTimeUtc()
                .toJavaLocalDateTime()
                .atZone(zone)

            val delay = Duration.between(now, target).toMillis()

            return if (delay < 0) 0 else delay
        }
}
