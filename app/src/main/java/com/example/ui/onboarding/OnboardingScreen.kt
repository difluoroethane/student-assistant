package com.example.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrutalButton
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.LocalBrutalPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (name: String, username: String, department: String, semester: String) -> Unit
) {
    val palette = LocalBrutalPalette.current
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var expandedDept by remember { mutableStateOf(false) }
    var selectedDept by remember { mutableStateOf("Computer Engineering") }
    val departments = listOf("Computer Engineering", "EXTC", "CSE", "Electrical", "Mechanical")

    var expandedSem by remember { mutableStateOf(false) }
    var selectedSem by remember { mutableStateOf("Semester III") }
    val semesters = listOf(
        "Semester I", "Semester II", "Semester III", "Semester IV",
        "Semester V", "Semester VI", "Semester VII", "Semester VIII"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "SETUP PROFILE",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
            color = palette.textPrimary
        )
        Text(
            text = "Welcome to Student Assistant & The Quad.",
            style = MaterialTheme.typography.titleMedium,
            color = palette.accentOrange
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username / Handle
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Campus Handle (e.g. BitFlipper)") },
            prefix = { Text("@", fontWeight = FontWeight.Black, color = palette.textPrimary) },
            placeholder = { Text("AnonCowboy", color = palette.textSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_username_input"),
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.border,
                unfocusedBorderColor = palette.border,
                focusedContainerColor = palette.surface,
                unfocusedContainerColor = palette.surface,
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Name (Optional)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_name_input"),
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.border,
                unfocusedBorderColor = palette.border,
                focusedContainerColor = palette.surface,
                unfocusedContainerColor = palette.surface,
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Department Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedDept,
            onExpandedChange = { expandedDept = !expandedDept },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedDept,
                onValueChange = {},
                readOnly = true,
                label = { Text("Department") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDept) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.border,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary
                )
            )
            ExposedDropdownMenu(
                expanded = expandedDept,
                onDismissRequest = { expandedDept = false },
                modifier = Modifier
                    .background(palette.surface)
                    .border(2.dp, palette.border, RectangleShape)
            ) {
                departments.forEach { dept ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                dept,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            selectedDept = dept
                            expandedDept = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Semester Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedSem,
            onExpandedChange = { expandedSem = !expandedSem },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedSem,
                onValueChange = {},
                readOnly = true,
                label = { Text("Semester") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSem) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.border,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary
                )
            )
            ExposedDropdownMenu(
                expanded = expandedSem,
                onDismissRequest = { expandedSem = false },
                modifier = Modifier
                    .background(palette.surface)
                    .border(2.dp, palette.border, RectangleShape)
            ) {
                semesters.forEach { sem ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                sem,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            selectedSem = sem
                            expandedSem = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        BrutalButton(
            onClick = {
                val effectiveUsername = if (username.isNotBlank()) username else "@AnonCowboy"
                onComplete(name, effectiveUsername, selectedDept, selectedSem)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_complete_button"),
            backgroundColor = BrutalOrange,
            contentColor = BrutalWhite
        ) {
            Text(
                "ENTER CAMPUS PORTAL",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
        }
    }
}
