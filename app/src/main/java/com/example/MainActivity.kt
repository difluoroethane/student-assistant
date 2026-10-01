package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.AppViewModelFactory
import com.example.ui.MainViewModel
import com.example.update.UpdateAvailableDialog
import com.example.ui.components.NetworkStatusBanner
import com.example.ui.forum.ForumScreen
import com.example.ui.forum.ForumViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.notes.NotesScreen
import com.example.ui.notes.NotesViewModel
import com.example.ui.notes.SubjectDetailScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.SplashScreen
import com.example.ui.planner.PlannerScreen
import com.example.ui.planner.PlannerViewModel
import com.example.ui.profile.ProfileScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.LocalBrutalPalette

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val app = application as StudentAssistantApp
                val factory = AppViewModelFactory(
                    repository = app.repository,
                    connectivityObserver = app.connectivityObserver,
                    externalNewsService = app.externalNewsService,
                    firestoreService = app.firestoreService,
                    dao = app.database.appDao()
                )
                val mainViewModel: MainViewModel = viewModel(factory = factory)

                StudentAssistantAppUI(mainViewModel, factory)
            }
        }
    }
}

@Composable
fun StudentAssistantAppUI(mainViewModel: MainViewModel, factory: AppViewModelFactory) {
    val isLoading by mainViewModel.isLoading.collectAsStateWithLifecycle()
    val userProfile by mainViewModel.userProfile.collectAsStateWithLifecycle()
    val updateInfo by mainViewModel.updateInfo.collectAsStateWithLifecycle()
    val isDownloadingUpdate by mainViewModel.isDownloadingUpdate.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showSplash by remember { mutableStateOf(true) }
    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
        return
    }

    if (!isLoading) {
        if (userProfile == null) {
            OnboardingScreen(
                onComplete = { name, username, dept, sem ->
                    mainViewModel.saveOnboardingData(name, username, dept, sem)
                }
            )
        } else {
            MainAppScaffold(userProfile!!, factory, mainViewModel)
        }
    }

    if (updateInfo != null) {
        UpdateAvailableDialog(
            updateInfo = updateInfo!!,
            isDownloading = isDownloadingUpdate,
            onDownloadAndInstall = {
                mainViewModel.startUpdateDownload(context)
            },
            onDismiss = {
                mainViewModel.dismissUpdate()
            }
        )
    }
}

@Composable
fun MainAppScaffold(
    userProfile: com.example.data.UserProfile,
    factory: AppViewModelFactory,
    mainViewModel: MainViewModel
) {
    val palette = LocalBrutalPalette.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val networkStatus by mainViewModel.networkStatus.collectAsStateWithLifecycle()

    var showBranchSwitcher by remember { mutableStateOf(false) }

    if (showBranchSwitcher) {
        OnboardingScreen(
            onComplete = { name, username, dept, sem ->
                mainViewModel.saveOnboardingData(name, username, dept, sem)
                showBranchSwitcher = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (currentDestination?.route != "profile" && currentDestination?.route?.startsWith("subject_detail") != true) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrutalOrange)
                            .statusBarsPadding()
                            .drawBehind {
                                drawLine(
                                    color = palette.border,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "STUDENT ASSISTANT",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp,
                                shadow = Shadow(
                                    color = BrutalBlack,
                                    offset = Offset(3f, 3f),
                                    blurRadius = 0f
                                )
                            ),
                            color = BrutalWhite
                        )
                        Spacer(modifier = Modifier.padding(1.dp))
                        Text(
                            text = "CAMPUS PLATFORM • ${userProfile.department.uppercase()}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .background(if (palette.isDark) BrutalBlack else BrutalWhite)
                                .border(1.dp, palette.border)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            color = if (palette.isDark) BrutalWhite else BrutalBlack
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!networkStatus.isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD32F2F))
                            .border(2.dp, BrutalBlack, RectangleShape)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("network_offline_banner"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(BrutalWhite, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NO INTERNET CONNECTION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = BrutalWhite
                            )
                        }
                    }
                }
                BottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                val vm: HomeViewModel = viewModel(factory = factory)
                HomeScreen(viewModel = vm)
            }
            composable("notes") {
                val vm: NotesViewModel = viewModel(factory = factory)
                NotesScreen(
                    viewModel = vm,
                    userProfile = userProfile,
                    onSubjectClick = { subjectId ->
                        navController.navigate("subject_detail/$subjectId")
                    }
                )
            }
            composable("subject_detail/{subjectId}") { backStackEntry ->
                val vm: NotesViewModel = viewModel(factory = factory)
                val subjectId = backStackEntry.arguments?.getString("subjectId")?.toIntOrNull()
                if (subjectId != null) {
                    SubjectDetailScreen(
                        subjectId = subjectId,
                        viewModel = vm,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable("forum") {
                val vm: ForumViewModel = viewModel(factory = factory)
                ForumScreen(
                    viewModel = vm,
                    userProfile = userProfile,
                    onUpdateUsername = { newUsername ->
                        mainViewModel.updateUsername(newUsername)
                    }
                )
            }
            composable("planner") {
                val vm: PlannerViewModel = viewModel(factory = factory)
                PlannerScreen(viewModel = vm)
            }
            composable("profile") {
                ProfileScreen(
                    userProfile = userProfile,
                    viewModel = mainViewModel,
                    onEditProfile = {
                        showBranchSwitcher = true
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val palette = LocalBrutalPalette.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier.drawBehind {
            drawLine(
                color = palette.border,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 3.dp.toPx()
            )
        },
        containerColor = palette.surface,
        contentColor = palette.textPrimary
    ) {
        val items = listOf(
            Triple("Home", "home", Icons.Default.Home),
            Triple("Notes", "notes", Icons.Default.Description),
            Triple("Forum", "forum", Icons.Default.Forum),
            Triple("Planner", "planner", Icons.Default.DateRange),
            Triple("Profile", "profile", Icons.Default.Person)
        )
        items.forEach { (title, route, icon) ->
            val selected = currentDestination?.hierarchy?.any { it.route == route } == true ||
                    (route == "notes" && currentDestination?.route?.startsWith("subject_detail") == true)
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = title) },
                label = {
                    Text(
                        title.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrutalOrange,
                    selectedTextColor = BrutalOrange,
                    unselectedIconColor = palette.textSecondary,
                    unselectedTextColor = palette.textSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
