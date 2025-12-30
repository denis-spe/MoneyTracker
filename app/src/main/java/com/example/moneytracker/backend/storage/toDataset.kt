package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp

fun Map<*, *>.toRepay(): Repay {
    val repayId = this["repayId"] as? String ?: ""
    val amount = (this["amount"] as? Number)?.toDouble()
        ?: (this["amount"] as? String)?.toDoubleOrNull()
        ?: 0.0
    val label = this["label"] as? String ?: ""
    val description = this["description"] as? String ?: ""
    val dateTime = when (val dateTimeRaw = this["dateTime"]) {
        is Timestamp -> dateTimeRaw
        is Map<*, *> -> {
            val seconds = (dateTimeRaw["seconds"] as? Number)?.toLong()
                ?: (dateTimeRaw["seconds"] as? String)?.toLongOrNull()
                ?: 0L
            val nanoseconds = (dateTimeRaw["nanoseconds"] as? Number)?.toInt()
                ?: (dateTimeRaw["nanoseconds"] as? String)?.toIntOrNull()
                ?: 0
            Timestamp(seconds, nanoseconds)
        }

        is Number -> Timestamp(dateTimeRaw.toLong(), 0)
        is String -> (dateTimeRaw.toLongOrNull()?.let { Timestamp(it, 0) }) ?: Timestamp(0, 0)
        else -> Timestamp(0, 0)
    }

    return Repay(
        repayId = repayId,
        amount = amount,
        label = label,
        description = description,
        dateTime = dateTime
    )
}

fun Map<*, *>.toDataset(): Dataset {
    // Parse dateTime which may be stored as a Timestamp, a map (with seconds/nanoseconds), or a numeric seconds value
    val dateTime = when (val dateTimeRaw = this["dateTime"]) {
        is Timestamp -> dateTimeRaw
        is Map<*, *> -> {
            val seconds = (dateTimeRaw["seconds"] as? Number)?.toLong()
                ?: (dateTimeRaw["seconds"] as? String)?.toLongOrNull()
                ?: 0L
            val nanoseconds = (dateTimeRaw["nanoseconds"] as? Number)?.toInt()
                ?: (dateTimeRaw["nanoseconds"] as? String)?.toIntOrNull()
                ?: 0
            Timestamp(seconds, nanoseconds)
        }

        is Number -> Timestamp(dateTimeRaw.toLong(), 0)
        is String -> (dateTimeRaw.toLongOrNull()?.let { Timestamp(it, 0) }) ?: Timestamp(0, 0)
        else -> Timestamp(0, 0)
    }

    // amount may be stored as Double or Long (Number) or String
    val amount = (this["amount"] as? Number)?.toDouble()
        ?: (this["amount"] as? String)?.toDoubleOrNull()
        ?: 0.0

    // dataType may be stored as a String (enum name), a Number (ordinal), or a map/pojo
    val dataType = when (val dt = this["dataType"]) {
        is String -> try {
            DataType.valueOf(dt)
        } catch (_: Exception) {
            DataType.EARNINGS
        }

        is Number -> DataType.entries.getOrNull(dt.toInt()) ?: DataType.EARNINGS
        is Map<*, *> -> {
            val name = (dt["name"] ?: dt["value"] ?: dt["text"]) as? String
            if (name != null) try {
                DataType.valueOf(name)
            } catch (_: Exception) {
                DataType.EARNINGS
            } else DataType.EARNINGS
        }

        else -> DataType.EARNINGS
    }

    val repay = when (val repayRaw = this["repay"]) {
        is List<*> -> repayRaw.mapNotNull { it as? Map<*, *> }.map { it.toRepay() }
        else -> null
    }

    val label = this["label"] as? String ?: ""
    val description = this["description"] as? String ?: ""

    val labelIcon = (this["labelIcon"] as? Number)?.toInt()
        ?: (this["labelIcon"] as? String)?.toIntOrNull()
        ?: 0

    return Dataset(
        amount = amount,
        dataType = dataType,
        label = label,
        description = description,
        dateTime = dateTime,
        labelIcon = labelIcon,
        repay = repay
    )
}