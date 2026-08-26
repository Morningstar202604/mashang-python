package com.pyneon.academy.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pyneon.academy.screens.ArenaScreen
import com.pyneon.academy.screens.BootScreen
import com.pyneon.academy.screens.CertificateScreen
import com.pyneon.academy.screens.ChallengeDetailScreen
import com.pyneon.academy.screens.ContentHubScreen
import com.pyneon.academy.screens.HomeScreen
import com.pyneon.academy.screens.LessonDetailScreen
import com.pyneon.academy.screens.LessonsScreen
import com.pyneon.academy.screens.ProfileScreen
import com.pyneon.academy.screens.TerminalScreen
import com.pyneon.academy.ui.theme.Bg1
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.TextDim

private data class TopDest(val route: String, val label: String, val icon: ImageVector)

private val TOP_DESTS = listOf(
    TopDest("home", "指挥台", Icons.Outlined.SpaceDashboard),
    TopDest("lessons", "数据流", Icons.AutoMirrored.Outlined.MenuBook),
    TopDest("terminal", "接口", Icons.Outlined.Terminal),
    TopDest("arena", "角斗场", Icons.Outlined.LocalFireDepartment),
    TopDest("profile", "档案", Icons.Outlined.Person)
)

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "boot"
    val showBar = TOP_DESTS.any { it.route == currentRoute }

    Scaffold(
        containerColor = Bg0,
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = Bg1) {
                    TOP_DESTS.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label, tint = if (currentRoute == dest.route) NeonCyan else TextDim) },
                            label = { Text(dest.label, style = MaterialTheme.typography.labelSmall, color = if (currentRoute == dest.route) NeonCyan else TextDim) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "boot",
            modifier = Modifier
                .padding(padding)
                .background(Bg0),
            enterTransition = {
                androidx.compose.animation.slideInHorizontally(
                    animationSpec = androidx.compose.animation.core.tween(260),
                    initialOffsetX = { it / 4 }
                ) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(260))
            },
            exitTransition = {
                androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(160))
            },
            popEnterTransition = {
                androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(200))
            },
            popExitTransition = {
                androidx.compose.animation.slideOutHorizontally(
                    animationSpec = androidx.compose.animation.core.tween(240),
                    targetOffsetX = { it / 4 }
                ) + androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(240))
            }
        ) {
            composable("boot") {
                BootScreen(onDone = {
                    navController.navigate("home") {
                        popUpTo("boot") { inclusive = true }
                    }
                })
            }
            composable("home") {
                HomeScreen(
                    onOpenLesson = { id -> navController.navigate("lesson/$id") },
                    onOpenTerminal = { navController.navigate("terminal") },
                    onOpenArena = { navController.navigate("arena") },
                    onOpenLessons = { navController.navigate("lessons") }
                )
            }
            composable("lessons") {
                LessonsScreen(openLesson = { id -> navController.navigate("lesson/$id") })
            }
            composable("lesson/{id}") { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                LessonDetailScreen(lessonId = id, onBack = { navController.popBackStack() })
            }
            composable("terminal") { TerminalScreen() }
            composable("arena") {
                ArenaScreen(openChallenge = { id -> navController.navigate("challenge/$id") })
            }
            composable("challenge/{id}") { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                ChallengeDetailScreen(challengeId = id, onBack = { navController.popBackStack() })
            }
            composable("profile") { ProfileScreen(
                onOpenContentHub = { navController.navigate("contenthub") },
                onOpenCertificate = { navController.navigate("certificate") }
            ) }
            composable("contenthub") { ContentHubScreen(onBack = { navController.popBackStack() }) }
            composable("certificate") { CertificateScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
