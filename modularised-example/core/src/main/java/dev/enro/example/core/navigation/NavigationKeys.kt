package dev.enro.example.core.navigation

import dev.enro.core.NavigationKey
import kotlinx.parcelize.Parcelize

@Parcelize
object LaunchKey: NavigationKey

@Parcelize
data class DashboardKey(val userId: String) : NavigationKey

@Parcelize
data class DetailKey(
    val userId: String,
    val id: String
) : NavigationKey.WithResult<Boolean>

enum class ListFilterType { ALL, MY_PUBLIC, MY_PRIVATE, ALL_PUBLIC, NOT_MY_PUBLIC }
@Parcelize
data class ListKey(
    val userId: String,
    val filter: ListFilterType
) : NavigationKey.WithResult<Boolean>

@Parcelize
class LoginKey : NavigationKey

@Parcelize
data class LoginErrorKey(val errorUser: String): NavigationKey

@Parcelize
class MasterDetailKey(
    val userId: String,
    val filter: ListFilterType
) : NavigationKey

@Parcelize
class MultiStackKey : NavigationKey

@Parcelize
data class SearchKey(
    val userId: String
) : NavigationKey

@Parcelize
data class UserKey(
    val userId: String
) : NavigationKey

