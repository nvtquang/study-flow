package com.example.studyflow.navigation

sealed class BottomNavDestination(
    val route: String,
    val label: String,
    val iconText: String
) {
    data object Home : BottomNavDestination("home", "Trang chủ", "H")
    data object Planner : BottomNavDestination("planner", "Kế hoạch", "K")
    data object AiAssistant : BottomNavDestination("ai_assistant", "AI", "AI")
    data object Focus : BottomNavDestination("focus", "Tập trung", "F")
    data object Files : BottomNavDestination("profile", "Hồ sơ", "P")
}

object AuthRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
}

object AppRoutes {
    const val ADD_SCHEDULE = "add_schedule"
    const val GOALS = "goals"
    const val GROUPS = "groups"
    const val FILES = "files"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val CHAT = "chat"
    const val CHAT_GROUP_ID = "groupId"

    fun chatRoute(groupId: String): String = "$CHAT/$groupId"
}

val bottomNavDestinations = listOf(
    BottomNavDestination.Home,
    BottomNavDestination.Planner,
    BottomNavDestination.AiAssistant,
    BottomNavDestination.Focus,
    BottomNavDestination.Files
)
