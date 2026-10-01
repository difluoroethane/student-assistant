package com.example.ui.home

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Announcement
import com.example.network.AcademicCircular
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalEmerald
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.CatGeneral
import com.example.ui.theme.LocalBrutalPalette

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val palette = LocalBrutalPalette.current
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val category by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val assignments by viewModel.nearestDeadline.collectAsStateWithLifecycle()
    val externalNews by viewModel.externalNews.collectAsStateWithLifecycle()
    val isLoadingNews by viewModel.isLoadingNews.collectAsStateWithLifecycle()

    val pendingAssignments = assignments.filter { it.status == "Pending" }
    val nearest = pendingAssignments.minByOrNull { it.dueDate }

    var showPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = palette.background,
        floatingActionButton = {
            val fabShadow = 4.dp
            Box(
                modifier = Modifier
                    .padding(bottom = fabShadow, end = fabShadow)
                    .drawBehind {
                        drawRect(
                            color = palette.shadow,
                            topLeft = Offset(fabShadow.toPx(), fabShadow.toPx()),
                            size = Size(size.width, size.height)
                        )
                    }
            ) {
                FloatingActionButton(
                    onClick = { showPostDialog = true },
                    shape = RectangleShape,
                    containerColor = BrutalOrange,
                    contentColor = BrutalWhite,
                    modifier = Modifier
                        .border(3.dp, palette.border, RectangleShape)
                        .testTag("post_announcement_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Post Notice", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "POST",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "CAMPUS HUB",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    Text(
                        "LATEST ANNOUNCEMENTS & CAMPUS NOTICES",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = palette.accentOrange
                    )
                }
            }

            // Up Next / Urgent Deadline Widget
            if (nearest != null) {
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "NEXT DEADLINE",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = palette.textPrimary
                            )
                            BrutalTag(
                                text = "${pendingAssignments.size} PENDING",
                                backgroundColor = BrutalNeonYellow,
                                textColor = BrutalBlack,
                                shadowOffset = 2.dp
                            )
                        }

                        BrutalCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderWidth = 3.dp,
                            shadowOffset = 5.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        "URGENT",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        modifier = Modifier
                                            .background(BrutalOrange)
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = BrutalWhite
                                    )
                                    Text(
                                        "DUE ${DateUtils.getRelativeTimeSpanString(nearest.dueDate).toString().uppercase()}",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = palette.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    nearest.title,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Priority: ${nearest.priority.uppercase()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = palette.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Campus Bulletin Widget (Clean notice board)
            if (externalNews.isNotEmpty() || isLoadingNews) {
                item {
                    AcademicCircularsWidget(
                        circulars = externalNews,
                        isLoading = isLoadingNews
                    )
                }
            }

            // Campus Announcements Header & Filter Chips
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "CAMPUS FEED",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = palette.textPrimary
                        )
                        Text(
                            "${announcements.size} POSTS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf("All", "Urgent", "Academic", "Event", "General")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = cat == category || (cat == "All" && category == null)
                            val chipBg = if (isSelected) BrutalOrange else palette.surface
                            val chipText = if (isSelected) BrutalWhite else palette.textPrimary

                            Box(
                                modifier = Modifier
                                    .border(2.dp, palette.border, RectangleShape)
                                    .background(chipBg, RectangleShape)
                                    .clickable { viewModel.selectCategory(cat) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("announcement_filter_${cat.lowercase()}")
                            ) {
                                Text(
                                    text = cat.uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    ),
                                    color = chipText
                                )
                            }
                        }
                    }
                }
            }

            // Empty state or announcements list
            if (announcements.isEmpty()) {
                item {
                    BrutalCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderWidth = 2.dp,
                        shadowOffset = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "NO ANNOUNCEMENTS YET",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = palette.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Tap 'POST' to publish the first notice or announcement.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                        }
                    }
                }
            } else {
                items(announcements, key = { it.id }) { announcement ->
                    AnnouncementCard(announcement = announcement)
                }
            }
        }
    }

    if (showPostDialog) {
        PostAnnouncementDialog(
            onDismiss = { showPostDialog = false },
            onSubmit = { title, description, cat, pinned ->
                viewModel.postAnnouncement(title, description, cat, pinned)
                showPostDialog = false
            }
        )
    }
}

@Composable
fun AcademicCircularsWidget(
    circulars: List<AcademicCircular>,
    isLoading: Boolean
) {
    val palette = LocalBrutalPalette.current

    BrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("academic_circulars_widget"),
        borderWidth = 3.dp,
        shadowOffset = 5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = BrutalEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "CAMPUS BULLETIN",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = palette.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    BrutalTag(
                        text = "NOTICE BOARD",
                        backgroundColor = BrutalEmerald,
                        textColor = BrutalBlack,
                        shadowOffset = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Latest updates and circulars from Mumbai University & college departments.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )

            if (circulars.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    circulars.take(3).forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, palette.border, RectangleShape)
                                .background(palette.background)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.source.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        color = palette.accentOrange
                                    )
                                    Text(
                                        text = item.date,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = palette.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.summary,
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                                    color = palette.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAnnouncementDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, category: String, pinned: Boolean) -> Unit
) {
    val palette = LocalBrutalPalette.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var pinned by remember { mutableStateOf(false) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, palette.border, RectangleShape)
                .testTag("post_announcement_dialog"),
            color = palette.surface,
            shape = RectangleShape
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "POST NOTICE",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title (e.g. Mid-term Exam Dates)") },
                    modifier = Modifier.fillMaxWidth().testTag("announcement_title_input"),
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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val categories = listOf("General", "Academic", "Event", "Urgent")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.5.dp, palette.border, RectangleShape)
                                .background(if (isSelected) BrutalOrange else palette.surface)
                                .clickable { category = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                cat.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = if (isSelected) BrutalWhite else palette.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details & Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("announcement_description_input"),
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

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, palette.border, RectangleShape)
                        .background(palette.background)
                        .clickable { pinned = !pinned }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = pinned,
                        onCheckedChange = { pinned = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BrutalOrange,
                            uncheckedColor = palette.textSecondary,
                            checkmarkColor = BrutalWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "PIN TO TOP OF FEED",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                BrutalButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSubmit(title, description, category, pinned)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("publish_notice_button"),
                    backgroundColor = BrutalOrange,
                    contentColor = BrutalWhite
                ) {
                    Text(
                        "PUBLISH NOTICE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    val palette = LocalBrutalPalette.current

    val iconVector = when (announcement.category) {
        "Urgent" -> Icons.Default.Warning
        "Event" -> Icons.Default.Event
        "Academic" -> Icons.Default.School
        else -> Icons.Default.Campaign
    }

    val iconBgColor = when (announcement.category) {
        "Urgent" -> BrutalOrange
        "Event" -> BrutalNeonYellow
        "Academic" -> CatAcademic
        else -> CatGeneral
    }

    val iconTextColor = if (announcement.category == "Event" || announcement.category == "Urgent") BrutalBlack else BrutalWhite

    BrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("announcement_card_${announcement.id}"),
        borderWidth = 3.dp,
        shadowOffset = 5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(iconBgColor, CircleShape)
                            .border(1.5.dp, BrutalBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = announcement.category,
                            tint = iconTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = announcement.category.uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            ),
                            color = palette.accentOrange
                        )
                        Text(
                            text = DateUtils.getRelativeTimeSpanString(announcement.timestamp).toString().uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = palette.textSecondary
                        )
                    }
                }

                if (announcement.pinned) {
                    BrutalTag(
                        text = "PINNED",
                        backgroundColor = BrutalNeonYellow,
                        textColor = BrutalBlack,
                        shadowOffset = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = palette.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.description,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = palette.textPrimary
            )
        }
    }
}
