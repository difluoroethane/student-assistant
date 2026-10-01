package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.LocalBrutalPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    viewModel: MainViewModel,
    onEditProfile: () -> Unit
) {
    val palette = LocalBrutalPalette.current
    var showEditHandleDialog by remember { mutableStateOf(false) }
    var showGpaDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "STUDENT PROFILE",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
            color = palette.textPrimary
        )
        Text(
            "IDENTIFIER & ACADEMIC STATUS",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = palette.accentOrange
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Student Card
        BrutalCard(
            modifier = Modifier.fillMaxWidth(),
            borderWidth = 3.dp,
            shadowOffset = 5.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .border(2.dp, palette.border, RectangleShape)
                            .background(BrutalNeonYellow, RectangleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            userProfile.username,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = BrutalBlack
                        )
                    }

                    IconButton(
                        onClick = { showEditHandleDialog = true },
                        modifier = Modifier.testTag("edit_handle_icon_button")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Handle",
                            tint = palette.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile.name ?: "Student User",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${userProfile.department} • ${userProfile.semester}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.accentOrange
                )

                Spacer(modifier = Modifier.height(20.dp))

                BrutalButton(
                    onClick = onEditProfile,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = palette.surface,
                    contentColor = palette.textPrimary
                ) {
                    Text(
                        "SWITCH BRANCH / SEMESTER",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "ACADEMIC UTILITIES",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            color = palette.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        BrutalCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showGpaDialog = true },
            borderWidth = 2.dp,
            shadowOffset = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .border(2.dp, palette.border, RectangleShape)
                        .background(BrutalOrange, RectangleShape)
                        .padding(10.dp)
                ) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = "GPA",
                        tint = BrutalWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        "GPA ESTIMATOR",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    Text(
                        "Credit weight calculator based on semester marks.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = palette.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "SYSTEM CONFIG",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            color = palette.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        var notificationsEnabled by remember { mutableStateOf(userProfile.notificationsEnabled) }

        BrutalCard(
            modifier = Modifier.fillMaxWidth(),
            borderWidth = 2.dp,
            shadowOffset = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "CAMPUS NOTIFICATIONS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    Text(
                        "Urgent alerts and deadline reminders",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = palette.textSecondary
                    )
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BrutalWhite,
                        checkedTrackColor = BrutalOrange,
                        uncheckedThumbColor = palette.textSecondary,
                        uncheckedTrackColor = palette.surface
                    )
                )
            }
        }
    }

    if (showEditHandleDialog) {
        EditHandleModal(
            currentHandle = userProfile.username,
            onDismiss = { showEditHandleDialog = false },
            onSave = { newHandle ->
                viewModel.updateUsername(newHandle)
                showEditHandleDialog = false
            }
        )
    }

    if (showGpaDialog) {
        GpaCalculatorDialog(onDismiss = { showGpaDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHandleModal(
    currentHandle: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val palette = LocalBrutalPalette.current
    var handle by remember { mutableStateOf(currentHandle.removePrefix("@")) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, palette.border, RectangleShape),
            shape = RectangleShape,
            color = palette.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "UPDATE HANDLE",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    prefix = { Text("@", fontWeight = FontWeight.Bold, color = palette.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.border,
                        unfocusedBorderColor = palette.border,
                        focusedContainerColor = palette.background,
                        unfocusedContainerColor = palette.background,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                BrutalButton(
                    onClick = {
                        val formatted = if (handle.isNotBlank()) "@$handle" else "@AnonCowboy"
                        onSave(formatted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BrutalNeonYellow,
                    contentColor = BrutalBlack
                ) {
                    Text(
                        "SAVE HANDLE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCalculatorDialog(onDismiss: () -> Unit) {
    val palette = LocalBrutalPalette.current

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, palette.border, RectangleShape),
            shape = RectangleShape,
            color = palette.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "GPA ESTIMATOR",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, palette.border, RectangleShape)
                        .background(BrutalNeonYellow, RectangleShape)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "ESTIMATED SGPA",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = BrutalBlack
                        )
                        Text(
                            "8.85 / 10.0",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                            color = BrutalBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Grade calculations are scaled against your department's grading scheme (A+: 10, A: 9, B+: 8, B: 7, C: 6).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                BrutalButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BrutalOrange,
                    contentColor = BrutalWhite
                ) {
                    Text(
                        "DONE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}
