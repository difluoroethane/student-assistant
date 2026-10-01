package com.example.ui.planner

import android.text.format.DateUtils
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalGreen
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.CatEvent
import com.example.ui.theme.CatGeneral
import com.example.ui.theme.LocalBrutalPalette

@Composable
fun PlannerScreen(viewModel: PlannerViewModel) {
    val palette = LocalBrutalPalette.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("TIMETABLE", "DEADLINES & EXAMS")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surface)
                .border(2.dp, palette.border, RectangleShape)
                .padding(16.dp)
        ) {
            Text(
                "ACADEMIC PLANNER",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                color = palette.textPrimary
            )
            Text(
                "SCHEDULE & EVALUATIONS",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = palette.accentOrange
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Neo-Brutalist Tabs
            Row(modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, palette.border, RectangleShape)
                            .background(if (isSelected) BrutalOrange else palette.surface, RectangleShape)
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("planner_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) BrutalWhite else palette.textPrimary
                        )
                    }
                    if (index < tabs.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }

        when (selectedTab) {
            0 -> TimetableTab(viewModel)
            1 -> DeadlinesTab(viewModel)
        }
    }
}

@Composable
fun TimetableTab(viewModel: PlannerViewModel) {
    val palette = LocalBrutalPalette.current
    val entries by viewModel.timetable.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (entries.isEmpty()) {
            item {
                BrutalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "NO LECTURES SCHEDULED",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = palette.textPrimary
                        )
                    }
                }
            }
        }

        items(entries) { entry ->
            BrutalCard(
                modifier = Modifier.fillMaxWidth(),
                borderWidth = 3.dp,
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
                            .background(BrutalNeonYellow, RectangleShape)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            entry.day.uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = BrutalBlack
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${entry.startTime} - ${entry.endTime}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = palette.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "LOCATION: ${entry.room ?: "Lab / Lecture Hall"}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = palette.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeadlinesTab(viewModel: PlannerViewModel) {
    val palette = LocalBrutalPalette.current
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "ACTIVE ASSIGNMENTS",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = palette.textPrimary
            )
        }

        items(assignments) { assignment ->
            val isSubmitted = assignment.status == "Submitted"
            BrutalCard(
                modifier = Modifier.fillMaxWidth(),
                borderWidth = 3.dp,
                shadowOffset = 4.dp,
                onClick = { viewModel.toggleAssignmentStatus(assignment) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BrutalTag(
                            text = "PRIORITY: ${assignment.priority}",
                            backgroundColor = if (assignment.priority == "High") BrutalOrange else BrutalNeonYellow,
                            textColor = if (assignment.priority == "High") BrutalWhite else BrutalBlack
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSubmitted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = BrutalGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (isSubmitted) "SUBMITTED" else "PENDING",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = if (isSubmitted) BrutalGreen else palette.accentOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "DUE: ${DateUtils.getRelativeTimeSpanString(assignment.dueDate).toString().uppercase()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = palette.textSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "UPCOMING EXAMINATIONS",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = palette.textPrimary
            )
        }

        items(exams) { exam ->
            BrutalCard(
                modifier = Modifier.fillMaxWidth(),
                borderWidth = 3.dp,
                shadowOffset = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BrutalTag(
                            text = "EXAM",
                            backgroundColor = BrutalNeonYellow,
                            textColor = BrutalBlack
                        )
                        Text(
                            text = "IN ${DateUtils.getRelativeTimeSpanString(exam.date).toString().uppercase()}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = exam.modulesCovered,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )

                    if (exam.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "NOTE: ${exam.notes}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textSecondary
                        )
                    }
                }
            }
        }
    }
}
