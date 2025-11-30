package com.example.moneytracker.ui.screenManager

import kotlinx.serialization.Serializable


@Serializable
object StartUpScreenRouter

@Serializable
data class LoadingScreenRouter(val userId: String)

@Serializable
object MailScreenRouter

@Serializable
object LoginScreenRouter

@Serializable
object EmailRegistrationScreenRouter

@Serializable
object NamesRegistrationScreenRouter

@Serializable
object PasswordRegistrationScreenRouter

@Serializable
data class HomeScreenRouter(val userId: String)