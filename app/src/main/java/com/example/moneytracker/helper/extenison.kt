// Great is the LORD of hosts
package com.example.moneytracker.helper

val Int.addZeroIfLessThenTen: String
    get() = if (this < 10) "0$this" else this.toString()

val String.title: String
    get() = lowercase().mapIndexedNotNull { index, c ->
        if (index == 0) c.uppercase() else c.lowercase()
    }.joinToString("")
