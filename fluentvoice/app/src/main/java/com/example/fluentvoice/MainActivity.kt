package com.example.fluentvoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.fluentvoice.data.repository.FluentVoiceRepository
import com.example.fluentvoice.ui.screens.*
import com.example.fluentvoice.ui.screens.patient.*
import com.example.fluentvoice.ui.screens.therapist.*
import com.example.fluentvoice.ui.theme.FluentvoiceTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FluentvoiceTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                val currentUser by FluentVoiceRepository.currentUser.collectAsState()

                // State representing the current destination route for top bar title and drawer highlights
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Automatic redirection on auth state updates
                LaunchedEffect(currentUser) {
                    val user = currentUser
                    if (user == null) {
                        // If logged out and not on landing/login/forgot-password, redirect to landing
                        if (currentRoute != "landing" && currentRoute != "forgot_password" && currentRoute?.startsWith("login") == false) {
                            navController.navigate("landing") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    } else {
                        // Logged in redirection if currently on landing/login
                        if (currentRoute == "landing" || currentRoute?.startsWith("login") == true) {
                            val target = if (user.role == "therapist") "therapist_dashboard" else "patient_dashboard"
                            navController.navigate(target) {
                                popUpTo("landing") { inclusive = true }
                            }
                        }
                    }
                }

                // Helper list of drawer screens depending on user role
                val drawerItems = remember(currentUser?.role) {
                    if (currentUser?.role == "therapist") {
                        listOf(
                            Triple("Dashboard", "therapist_dashboard", Icons.Default.Dashboard),
                            Triple("My Patients", "therapist_patients", Icons.Default.People),
                            Triple("Clinic Bookings", "therapist_appointments", Icons.Default.Event),
                            Triple("Profile Settings", "therapist_profile", Icons.Default.Settings)
                        )
                    } else {
                        listOf(
                            Triple("Dashboard", "patient_dashboard", Icons.Default.Dashboard),
                            Triple("Record Speech", "patient_record", Icons.Default.Mic),
                            Triple("Session History", "patient_sessions", Icons.Default.History),
                            Triple("Appointments", "patient_appointments", Icons.Default.Event),
                            Triple("Treatment Plan", "patient_treatment", Icons.Default.Assignment),
                            Triple("Profile Settings", "patient_profile", Icons.Default.Settings)
                        )
                    }
                }

                // Drawer wrapper (Only active when logged in and on a main dashboard screen)
                val showDrawerScaffold = currentUser != null && currentRoute != null && 
                        (currentRoute.startsWith("patient") || currentRoute.startsWith("therapist"))

                if (showDrawerScaffold) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.width(280.dp),
                                drawerContainerColor = Color.White
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                // Drawer Header
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = currentUser?.name ?: "User",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1B2B5E)
                                    )
                                    Text(
                                        text = (currentUser?.role ?: "patient").uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFFC9A84C),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Divider(color = Color(0xFFC8D3E8).copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))

                                // Nav items
                                drawerItems.forEach { item ->
                                    val (label, route, icon) = item
                                    val isSelected = currentRoute == route
                                    NavigationDrawerItem(
                                        label = { Text(text = label, fontWeight = FontWeight.Bold) },
                                        selected = isSelected,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate(route) {
                                                popUpTo(if (currentUser?.role == "therapist") "therapist_dashboard" else "patient_dashboard") {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(imageVector = icon, contentDescription = null) },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = Color(0xFF1B2B5E).copy(alpha = 0.08f),
                                            selectedIconColor = Color(0xFF1B2B5E),
                                            selectedTextColor = Color(0xFF1B2B5E),
                                            unselectedIconColor = Color(0xFF9CA3AF),
                                            unselectedTextColor = Color(0xFF64748B)
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Logout Button
                                NavigationDrawerItem(
                                    label = { Text(text = "Sign Out", fontWeight = FontWeight.Bold) },
                                    selected = false,
                                    onClick = {
                                        coroutineScope.launch {
                                            drawerState.close()
                                            FluentVoiceRepository.logout()
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedIconColor = Color(0xFFEF4444),
                                        unselectedTextColor = Color(0xFFEF4444)
                                    ),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Text(
                                            text = when (currentRoute) {
                                                "patient_dashboard" -> "Dashboard"
                                                "patient_record" -> "Speech Analysis"
                                                "patient_sessions" -> "Sessions"
                                                "patient_appointments" -> "Appointments"
                                                "patient_treatment" -> "Treatment"
                                                "patient_profile" -> "Profile"
                                                "therapist_dashboard" -> "Dashboard"
                                                "therapist_patients" -> "Directory"
                                                "therapist_appointments" -> "Bookings"
                                                "therapist_profile" -> "Profile"
                                                else -> "FluentVoice"
                                            },
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            coroutineScope.launch {
                                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                            }
                                        }) {
                                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                        }
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = Color(0xFF1B2B5E)
                                    )
                                )
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                NavigationGraph(navController)
                            }
                        }
                    }
                } else {
                    // Raw scaffold for log-out views
                    Scaffold { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavigationGraph(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: androidx.navigation.NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        composable("landing") {
            LandingScreen(
                onNavigateToLogin = { role ->
                    val path = if (role != null) "login?role=$role" else "login"
                    navController.navigate(path)
                }
            )
        }

        composable(
            route = "login?role={role}",
            arguments = listOf(navArgument("role") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            LoginScreen(
                initialRole = role,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onAuthSuccess = { userRole ->
                    val dest = if (userRole == "therapist") "therapist_dashboard" else "patient_dashboard"
                    navController.navigate(dest) {
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Patient Portal Composables
        composable("patient_dashboard") {
            PatientDashboardScreen(
                onNavigateToRecord = { navController.navigate("patient_record") },
                onNavigateToAppointments = { navController.navigate("patient_appointments") },
                onNavigateToSessions = { navController.navigate("patient_sessions") },
                onNavigateToTreatment = { navController.navigate("patient_treatment") }
            )
        }

        composable("patient_record") {
            PatientRecordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("patient_sessions") {
            PatientSessionsScreen()
        }

        composable("patient_appointments") {
            PatientAppointmentsScreen()
        }

        composable("patient_treatment") {
            PatientTreatmentScreen()
        }

        composable("patient_profile") {
            PatientProfileScreen()
        }

        // Therapist Portal Composables
        composable("therapist_dashboard") {
            TherapistDashboardScreen(
                onNavigateToPatients = { navController.navigate("therapist_patients") },
                onNavigateToAppointments = { navController.navigate("therapist_appointments") }
            )
        }

        composable("therapist_patients") {
            TherapistPatientsScreen(
                onNavigateToPatientDetails = { patientId ->
                    navController.navigate("therapist_patient_details/$patientId")
                }
            )
        }

        composable(
            route = "therapist_patient_details/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            TherapistPatientDetailsScreen(
                patientId = patientId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("therapist_appointments") {
            TherapistAppointmentsScreen()
        }

        composable("therapist_profile") {
            TherapistProfileScreen()
        }
    }
}