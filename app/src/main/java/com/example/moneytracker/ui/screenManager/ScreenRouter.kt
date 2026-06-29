package com.example.moneytracker.ui.screenManager

import kotlinx.serialization.Serializable


@Serializable
object StartUpScreenRouter

@Serializable
data class LoadingScreenRouter(val userId: String)

@Serializable
object MailScreenRouter

@Serializable
object ShowAllTransactionsScreenRouter

@Serializable
object ShowAllLiabilitiesScreenRouter

@Serializable
object ShowAllGoalsScreenRouter


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

@Serializable
object SettingsScreenRouter

@Serializable
data class FulfillmentDetailScreenRouter(val goalId: String)

@Serializable
data class TransactionDetailScreenRouter(val transactionId: String)

@Serializable
data class LiabilityDetailScreenRouter(val liabilityId: String)

