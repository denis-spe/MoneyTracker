package com.example.moneytracker.ui.screenManager

import kotlinx.serialization.Serializable


@Serializable
object StartUpScreenRouter

@Serializable
object GoogleScreenRouter

@Serializable
object MailScreenRouter

@Serializable
object LoginScreenRouter

@Serializable
data class HomeScreenRouter(val userId: String)