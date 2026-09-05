package com.yatharth.whatsappscheduler.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yatharth.whatsappscheduler.ui.contacts.ContactPickerScreen
import com.yatharth.whatsappscheduler.ui.details.MessageDetailsScreen
import com.yatharth.whatsappscheduler.ui.home.HomeScreen
import com.yatharth.whatsappscheduler.ui.schedule.ScheduleScreen
import com.yatharth.whatsappscheduler.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Schedule : Screen("schedule?contactName={contactName}&phoneNumber={phoneNumber}") {
        fun createRoute(contactName: String = "", phoneNumber: String = ""): String {
            return "schedule?contactName=$contactName&phoneNumber=$phoneNumber"
        }
    }
    data object ContactPicker : Screen("contact_picker")
    data object MessageDetails : Screen("details/{messageId}") {
        fun createRoute(messageId: Long) = "details/$messageId"
    }
    data object Settings : Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSchedule = { name, phone ->
                    navController.navigate(Screen.Schedule.createRoute(name, phone))
                },
                onNavigateToDetails = { messageId ->
                    navController.navigate(Screen.MessageDetails.createRoute(messageId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Schedule.route,
            arguments = listOf(
                navArgument("contactName") { type = NavType.StringType; defaultValue = "" },
                navArgument("phoneNumber") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName") ?: ""
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            ScheduleScreen(
                initialContactName = contactName,
                initialPhoneNumber = phoneNumber,
                onNavigateToContactPicker = {
                    navController.navigate(Screen.ContactPicker.route)
                },
                onScheduleCompleted = {
                    navController.popBackStack(Screen.Home.route, false)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ContactPicker.route) {
            ContactPickerScreen(
                onContactSelected = { contact ->
                    navController.navigate(Screen.Schedule.createRoute(contact.name, contact.phoneNumber)) {
                        popUpTo(Screen.Schedule.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MessageDetails.route,
            arguments = listOf(navArgument("messageId") { type = NavType.LongType })
        ) {
            MessageDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
