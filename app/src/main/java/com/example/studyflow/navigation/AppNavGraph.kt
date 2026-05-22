package com.example.studyflow.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyflow.ui.components.StudyFlowBottomBar
import com.example.studyflow.ui.components.StudyFlowTopBar
import com.example.studyflow.ui.screens.AddScheduleScreen
import com.example.studyflow.ui.screens.AiAssistantScreen
import com.example.studyflow.ui.screens.ChatScreen
import com.example.studyflow.ui.screens.FilesScreen
import com.example.studyflow.ui.screens.FocusScreen
import com.example.studyflow.ui.screens.GoalsScreen
import com.example.studyflow.ui.screens.GroupsScreen
import com.example.studyflow.ui.screens.HomeScreen
import com.example.studyflow.ui.screens.LoginScreen
import com.example.studyflow.ui.screens.NotificationSettingsScreen
import com.example.studyflow.ui.screens.PlannerScreen
import com.example.studyflow.ui.screens.ProfileScreen
import com.example.studyflow.ui.screens.RegisterScreen
import com.example.studyflow.viewmodel.AddScheduleViewModel
import com.example.studyflow.viewmodel.AiAssistantViewModel
import com.example.studyflow.viewmodel.AuthViewModel
import com.example.studyflow.viewmodel.ChatViewModel
import com.example.studyflow.viewmodel.FilesViewModel
import com.example.studyflow.viewmodel.FocusViewModel
import com.example.studyflow.viewmodel.GoalsViewModel
import com.example.studyflow.viewmodel.GroupsViewModel
import com.example.studyflow.viewmodel.HomeViewModel
import com.example.studyflow.viewmodel.NotificationSettingsViewModel
import com.example.studyflow.viewmodel.PlannerViewModel
import com.example.studyflow.viewmodel.ProfileViewModel

@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    if (authState.isAuthenticated) {
        MainNavGraph(onLogout = authViewModel::signOut)
    } else {
        AuthNavGraph(authViewModel = authViewModel)
    }
}

@Composable
private fun AuthNavGraph(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.LOGIN
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                authState = authState,
                onLoginClick = authViewModel::signIn,
                onRegisterClick = {
                    authViewModel.clearError()
                    navController.navigate(AuthRoutes.REGISTER)
                },
                onForgotPasswordClick = {
                    authViewModel.clearError()
                },
                onGoogleClick = {
                    authViewModel.clearError()
                }
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                authState = authState,
                onRegisterClick = authViewModel::register,
                onLoginClick = {
                    authViewModel.clearError()
                    navController.popBackStack()
                },
                onGoogleClick = {
                    authViewModel.clearError()
                }
            )
        }
    }
}

@Composable
private fun MainNavGraph(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            val currentRoute = currentDestination?.route
            if (
                currentRoute != BottomNavDestination.Home.route &&
                currentRoute != AppRoutes.ADD_SCHEDULE &&
                currentRoute != AppRoutes.GOALS &&
                currentRoute != AppRoutes.GROUPS &&
                currentRoute != AppRoutes.FILES &&
                currentRoute != AppRoutes.NOTIFICATION_SETTINGS &&
                currentRoute?.startsWith(AppRoutes.CHAT) != true
            ) {
                val title = bottomNavDestinations
                    .firstOrNull { it.route == currentRoute }
                    ?.label
                    ?: BottomNavDestination.Home.label

                StudyFlowTopBar(
                    title = title,
                    subtitle = "StudyFlow",
                    actionText = "Thoát",
                    onActionClick = onLogout
                )
            }
        },
        bottomBar = {
            if (bottomNavDestinations.any { it.route == currentDestination?.route }) {
                StudyFlowBottomBar(
                    destinations = bottomNavDestinations,
                    selectedRoute = currentDestination?.hierarchy?.firstOrNull { destination ->
                        bottomNavDestinations.any { it.route == destination.route }
                    }?.route,
                    onDestinationClick = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavDestination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavDestination.Home.route) {
                HomeRoute(
                    onFocusClick = {
                        navController.navigate(BottomNavDestination.Focus.route) {
                            launchSingleTop = true
                        }
                    },
                    onGoalsClick = {
                        navController.navigate(AppRoutes.GOALS)
                    },
                    onGroupsClick = {
                        navController.navigate(AppRoutes.GROUPS)
                    }
                )
            }
            composable(BottomNavDestination.Planner.route) { backStackEntry ->
                val shouldRefresh by backStackEntry.savedStateHandle
                    .getStateFlow("planner_refresh", false)
                    .collectAsState()
                PlannerRoute(
                    shouldRefresh = shouldRefresh,
                    onRefreshHandled = {
                        backStackEntry.savedStateHandle["planner_refresh"] = false
                    },
                    onAddClick = {
                        navController.navigate(AppRoutes.ADD_SCHEDULE)
                    }
                )
            }
            composable(BottomNavDestination.AiAssistant.route) {
                AiAssistantRoute()
            }
            composable(AppRoutes.GROUPS) {
                GroupsRoute(
                    onGroupClick = { groupId ->
                        navController.navigate(AppRoutes.chatRoute(groupId))
                    }
                )
            }
            composable(BottomNavDestination.Focus.route) {
                FocusRoute()
            }
            composable(BottomNavDestination.Files.route) {
                ProfileRoute(
                    onGoalsClick = { navController.navigate(AppRoutes.GOALS) },
                    onGroupsClick = { navController.navigate(AppRoutes.GROUPS) },
                    onSettingsClick = { navController.navigate(AppRoutes.NOTIFICATION_SETTINGS) },
                    onFilesClick = { navController.navigate(AppRoutes.FILES) },
                    onLogoutClick = onLogout
                )
            }
            composable(AppRoutes.ADD_SCHEDULE) {
                AddScheduleRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("planner_refresh", true)
                        navController.popBackStack()
                    }
                )
            }
            composable(AppRoutes.GOALS) {
                GoalsRoute(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(AppRoutes.FILES) {
                FilesRoute()
            }
            composable(AppRoutes.NOTIFICATION_SETTINGS) {
                NotificationSettingsRoute(onBackClick = { navController.popBackStack() })
            }
            composable(
                route = "${AppRoutes.CHAT}/{${AppRoutes.CHAT_GROUP_ID}}",
                arguments = listOf(navArgument(AppRoutes.CHAT_GROUP_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString(AppRoutes.CHAT_GROUP_ID).orEmpty()
                ChatRoute(
                    groupId = groupId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun ProfileRoute(
    profileViewModel: ProfileViewModel = viewModel(),
    onGoalsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val profileState by profileViewModel.uiState.collectAsState()

    ProfileScreen(
        state = profileState,
        onEditName = profileViewModel::updateDisplayName,
        onGoalsClick = onGoalsClick,
        onGroupsClick = onGroupsClick,
        onSettingsClick = onSettingsClick,
        onFilesClick = onFilesClick,
        onLogoutClick = onLogoutClick,
        onRetry = profileViewModel::loadProfile,
        onMessageShown = profileViewModel::clearMessage
    )
}

@Composable
private fun NotificationSettingsRoute(
    viewModel: NotificationSettingsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    NotificationSettingsScreen(
        state = state,
        onBackClick = onBackClick,
        onClassRemindersChange = { checked -> viewModel.update { it.copy(classReminders = checked) } },
        onDeadlineAlertsChange = { checked -> viewModel.update { it.copy(deadlineAlerts = checked) } },
        onFocusRemindersChange = { checked -> viewModel.update { it.copy(focusReminders = checked) } },
        onGroupChatNotificationsChange = { checked -> viewModel.update { it.copy(groupChatNotifications = checked) } },
        onDailySummaryChange = { checked -> viewModel.update { it.copy(dailySummary = checked) } },
        onSummaryTimeChange = { time -> viewModel.update { it.copy(summaryTime = time) } },
        onRetry = viewModel::loadSettings,
        onMessageShown = viewModel::clearMessage
    )
}

@Composable
private fun AiAssistantRoute(
    aiAssistantViewModel: AiAssistantViewModel = viewModel()
) {
    val aiState by aiAssistantViewModel.uiState.collectAsState()

    AiAssistantScreen(
        state = aiState,
        onInputChange = aiAssistantViewModel::updateInput,
        onSendClick = aiAssistantViewModel::sendMessage
    )
}

@Composable
private fun GroupsRoute(
    groupsViewModel: GroupsViewModel = viewModel(),
    onGroupClick: (String) -> Unit
) {
    val groupsState by groupsViewModel.uiState.collectAsState()

    GroupsScreen(
        state = groupsState,
        onSearchChange = groupsViewModel::updateQuery,
        onCreateGroup = groupsViewModel::createGroup,
        onGroupClick = { group -> onGroupClick(group.id) },
        onRetry = groupsViewModel::listenGroups,
        onMessageShown = groupsViewModel::clearMessage
    )
}

@Composable
private fun ChatRoute(
    groupId: String,
    chatViewModel: ChatViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val chatState by chatViewModel.uiState.collectAsState()

    LaunchedEffect(groupId) {
        chatViewModel.start(groupId)
    }

    ChatScreen(
        state = chatState,
        onBackClick = onBackClick,
        onInputChange = chatViewModel::updateInput,
        onSendClick = chatViewModel::sendMessage,
        onMessageShown = chatViewModel::clearMessage
    )
}

@Composable
private fun HomeRoute(
    homeViewModel: HomeViewModel = viewModel(),
    onFocusClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onGroupsClick: () -> Unit
) {
    val homeState by homeViewModel.uiState.collectAsState()

    HomeScreen(
        state = homeState,
        onRetry = homeViewModel::loadHome,
        onFocusClick = onFocusClick,
        onGoalsClick = onGoalsClick,
        onGroupsClick = onGroupsClick
    )
}

@Composable
private fun PlannerRoute(
    plannerViewModel: PlannerViewModel = viewModel(),
    shouldRefresh: Boolean,
    onRefreshHandled: () -> Unit,
    onAddClick: () -> Unit
) {
    val plannerState by plannerViewModel.uiState.collectAsState()

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            plannerViewModel.refresh()
            onRefreshHandled()
        }
    }

    PlannerScreen(
        state = plannerState,
        onDateSelected = plannerViewModel::selectDate,
        onRetry = plannerViewModel::refresh,
        onAddClick = onAddClick
    )
}

@Composable
private fun FilesRoute(
    filesViewModel: FilesViewModel = viewModel()
) {
    val filesState by filesViewModel.uiState.collectAsState()

    FilesScreen(
        state = filesState,
        onSearchChange = filesViewModel::updateQuery,
        onSortSelected = filesViewModel::selectSort,
        onUploadFile = filesViewModel::uploadFile,
        onRetry = filesViewModel::loadFiles,
        onMessageShown = filesViewModel::clearMessage
    )
}

@Composable
private fun FocusRoute(
    focusViewModel: FocusViewModel = viewModel()
) {
    val focusState by focusViewModel.uiState.collectAsState()

    FocusScreen(
        state = focusState,
        onStart = focusViewModel::start,
        onPause = focusViewModel::pause,
        onResume = focusViewModel::resume,
        onReset = focusViewModel::reset,
        onRetry = focusViewModel::retry,
        onMessageShown = focusViewModel::clearMessage
    )
}

@Composable
private fun GoalsRoute(
    goalsViewModel: GoalsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val goalsState by goalsViewModel.uiState.collectAsState()

    GoalsScreen(
        state = goalsState,
        onBackClick = onBackClick,
        onFilterSelected = goalsViewModel::selectFilter,
        onAddGoal = goalsViewModel::addGoal,
        onToggleGoal = goalsViewModel::toggleGoal,
        onDeleteGoal = goalsViewModel::deleteGoal,
        onRetry = goalsViewModel::loadGoals,
        onMessageShown = goalsViewModel::clearMessage
    )
}

@Composable
private fun AddScheduleRoute(
    addScheduleViewModel: AddScheduleViewModel = viewModel(),
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val addState by addScheduleViewModel.uiState.collectAsState()

    AddScheduleScreen(
        state = addState,
        onTypeSelected = addScheduleViewModel::selectType,
        onTitleChange = addScheduleViewModel::updateTitle,
        onDateChange = addScheduleViewModel::updateDate,
        onStartTimeChange = addScheduleViewModel::updateStartTime,
        onEndTimeChange = addScheduleViewModel::updateEndTime,
        onLocationChange = addScheduleViewModel::updateLocation,
        onNoteChange = addScheduleViewModel::updateNote,
        onSaveClick = addScheduleViewModel::save,
        onSaved = {
            addScheduleViewModel.clearSaveSuccess()
            onSaved()
        },
        onBackClick = onBackClick
    )
}
