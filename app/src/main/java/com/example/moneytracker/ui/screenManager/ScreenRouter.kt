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
object EmailRegistrationScreenRouter

@Serializable
data class NamesRegistrationScreenRouter(val email: String)

@Serializable
data class PasswordRegistrationScreenRouter(
    val email: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class HomeScreenRouter(val userId: String)