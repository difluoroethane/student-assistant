package com.example.ui.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SharedNote
import com.example.data.Subject
import com.example.data.UserProfile
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.components.NoteDownloadProgressIndicator
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.LocalBrutalPalette

private fun queryFileName(context: Context, uri: Uri): String {
    var result: String? = null
    try {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex)
                }
            }
        } else if (uri.scheme == "file") {
            result = uri.lastPathSegment
        }
    } catch (ignored: Exception) {}
    return result ?: "note_attachment"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    userProfile: UserProfile,
    onSubjectClick: (Int) -> Unit
) {
    val palette = LocalBrutalPalette.current
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val sharedNotes by viewModel.sharedNotes.collectAsStateWithLifecycle()
    val currentTab by viewModel.notesTab.collectAsStateWithLifecycle()
    val isUploading by viewModel.isUploading.collectAsStateWithLifecycle()
    val uploadError by viewModel.uploadError.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedNoteForDetail by remember { mutableStateOf<SharedNote?>(null) }

    LaunchedEffect(userProfile) {
        viewModel.setParams(userProfile.department, userProfile.semester)
    }

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
                    onClick = { showUploadDialog = true },
                    shape = RectangleShape,
                    containerColor = BrutalOrange,
                    contentColor = BrutalWhite,
                    modifier = Modifier
                        .border(3.dp, palette.border, RectangleShape)
                        .testTag("upload_note_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Upload Notes", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Upload Notes",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(palette.background)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(2.dp, palette.border, RectangleShape)
                    .padding(16.dp)
            ) {
                Text(
                    "NOTES REPOSITORY",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary
                )
                Text(
                    "${userProfile.department.uppercase()} • ${userProfile.semester.uppercase()}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = palette.accentOrange
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Neo-Brutalist Toggle Tabs
                Row(modifier = Modifier.fillMaxWidth()) {
                    val tab1Active = currentTab == 0
                    val tab2Active = currentTab == 1

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, palette.border, RectangleShape)
                            .background(if (tab1Active) BrutalOrange else palette.surface, RectangleShape)
                            .clickable { viewModel.setNotesTab(0) }
                            .padding(vertical = 10.dp)
                            .testTag("tab_my_syllabus"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "MY SYLLABUS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = if (tab1Active) BrutalWhite else palette.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, palette.border, RectangleShape)
                            .background(if (tab2Active) BrutalOrange else palette.surface, RectangleShape)
                            .clickable { viewModel.setNotesTab(1) }
                            .padding(vertical = 10.dp)
                            .testTag("tab_community_notes"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "COMMUNITY NOTES",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = if (tab2Active) BrutalWhite else palette.textPrimary
                        )
                    }
                }
            }

            // Tab Content
            if (currentTab == 0) {
                // My Subject Syllabus
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (subjects.isEmpty()) {
                        item {
                            BrutalCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "NO SUBJECTS REGISTERED",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = palette.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "No syllabus records found. Tap 'Upload Notes' below to upload study material and add your subjects.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = palette.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    items(subjects, key = { it.id }) { subject ->
                        SubjectCard(subject = subject, onClick = { onSubjectClick(subject.id) })
                    }
                }
            } else {
                // Community Shared Notes Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Upload Action Card
                    item {
                        BrutalCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = palette.surface,
                            borderWidth = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "SHARE YOUR NOTES",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = palette.textPrimary
                                    )
                                    Text(
                                        "Share PDFs, diagrams, and study guides with fellow students.",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = palette.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                BrutalButton(
                                    onClick = { showUploadDialog = true },
                                    backgroundColor = BrutalNeonYellow,
                                    contentColor = BrutalBlack,
                                    modifier = Modifier.testTag("upload_note_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Upload Notes", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Upload Notes",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                                    )
                                }
                            }
                        }
                    }

                    if (sharedNotes.isEmpty()) {
                        item {
                            BrutalCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "NO COMMUNITY NOTES YET",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = palette.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Be the first to share your notes! Tap 'Upload Notes' above to attach your files.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = palette.textSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(sharedNotes, key = { it.id }) { note ->
                            val status = downloadStates[note.id] ?: DownloadStatus.Idle
                            CommunityNoteCard(
                                note = note,
                                downloadStatus = status,
                                onDownload = {
                                    viewModel.downloadNoteFile(
                                        context = context,
                                        noteKey = note.id,
                                        downloadUrl = note.downloadUrl,
                                        title = note.title,
                                        autoOpen = true
                                    )
                                },
                                onOpen = {
                                    if (status is DownloadStatus.Completed) {
                                        viewModel.openDownloadedFile(context, status.fileUri, status.localPath)
                                    }
                                },
                                onUpvote = { viewModel.upvoteSharedNote(note.id, userProfile.username) },
                                onReadDetail = { selectedNoteForDetail = note }
                            )
                        }
                    }
                }
            }
        }
    }

    // Upload Notes Dialog
    if (showUploadDialog) {
        UploadNotesDialog(
            userProfile = userProfile,
            isUploading = isUploading,
            uploadError = uploadError,
            onDismiss = { showUploadDialog = false },
            onSubmit = { subject, module, title, fileUri, fileName ->
                viewModel.uploadNoteWithFile(
                    subjectName = subject,
                    moduleTitle = module,
                    title = title,
                    fileUri = fileUri,
                    fileName = fileName,
                    uploaderName = userProfile.username,
                    onSuccess = {
                        showUploadDialog = false
                    },
                    onError = {}
                )
            }
        )
    }

    // Detail Modal Dialog
    if (selectedNoteForDetail != null) {
        val detailNote = selectedNoteForDetail!!
        val detailStatus = downloadStates[detailNote.id] ?: DownloadStatus.Idle
        NoteDetailDialog(
            note = detailNote,
            downloadStatus = detailStatus,
            onDownload = {
                viewModel.downloadNoteFile(
                    context = context,
                    noteKey = detailNote.id,
                    downloadUrl = detailNote.downloadUrl,
                    title = detailNote.title,
                    autoOpen = true
                )
            },
            onOpen = {
                if (detailStatus is DownloadStatus.Completed) {
                    viewModel.openDownloadedFile(context, detailStatus.fileUri, detailStatus.localPath)
                }
            },
            onDismiss = { selectedNoteForDetail = null },
            onUpvote = { viewModel.upvoteSharedNote(detailNote.id, userProfile.username) }
        )
    }
}

@Composable
fun SubjectCard(subject: Subject, onClick: () -> Unit) {
    val palette = LocalBrutalPalette.current

    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
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
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = BrutalBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary
                )
                if (!subject.code.isNullOrBlank()) {
                    Text(
                        text = "CODE: ${subject.code.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = palette.accentOrange
                    )
                }
            }

            BrutalTag(
                text = "VIEW",
                backgroundColor = palette.surface,
                textColor = palette.textPrimary,
                shadowOffset = 2.dp
            )
        }
    }
}

@Composable
fun CommunityNoteCard(
    note: SharedNote,
    downloadStatus: DownloadStatus = DownloadStatus.Idle,
    onDownload: () -> Unit = {},
    onOpen: () -> Unit = {},
    onUpvote: () -> Unit,
    onReadDetail: () -> Unit
) {
    val palette = LocalBrutalPalette.current
    val isRemoteUrl = note.downloadUrl.startsWith("http://") || note.downloadUrl.startsWith("https://")

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
                    text = note.subjectName.uppercase(),
                    backgroundColor = CatAcademic,
                    textColor = BrutalWhite
                )

                if (note.moduleTitle.isNotBlank()) {
                    Text(
                        text = note.moduleTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        ),
                        color = palette.accentOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = palette.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Uploaded by ${note.uploaderName}",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Attachment indicator with download progress or text preview
            if (isRemoteUrl) {
                NoteDownloadProgressIndicator(
                    status = downloadStatus,
                    onDownload = onDownload,
                    onOpen = onOpen,
                    testTagPrefix = "note_${note.id}"
                )
            } else {
                Text(
                    text = note.downloadUrl,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.textPrimary,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upvote
                Row(
                    modifier = Modifier
                        .border(2.dp, palette.border, RectangleShape)
                        .background(palette.surface, RectangleShape)
                        .clickable { onUpvote() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("upvote_note_${note.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Upvote Note",
                        tint = BrutalOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${note.upvotes} HELPFUL",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                }

                // Read Note
                Row(
                    modifier = Modifier
                        .border(2.dp, palette.border, RectangleShape)
                        .background(palette.surface, RectangleShape)
                        .clickable { onReadDetail() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("read_note_${note.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Read",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VIEW STUDY NOTES",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = palette.textPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadNotesDialog(
    userProfile: UserProfile,
    isUploading: Boolean,
    uploadError: String?,
    onDismiss: () -> Unit,
    onSubmit: (subject: String, module: String, title: String, fileUri: Uri, fileName: String) -> Unit
) {
    val context = LocalContext.current
    val palette = LocalBrutalPalette.current
    var subject by remember { mutableStateOf("") }
    var module by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = queryFileName(context, uri)
            localError = null
            if (title.isBlank()) {
                title = selectedFileName.substringBeforeLast('.')
            }
        }
    }

    BasicAlertDialog(onDismissRequest = { if (!isUploading) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, palette.border, RectangleShape)
                .testTag("upload_notes_dialog"),
            shape = RectangleShape,
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Upload Notes",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    if (!isUploading) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Distributed Systems)") },
                    modifier = Modifier.fillMaxWidth().testTag("upload_note_subject_input"),
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

                OutlinedTextField(
                    value = module,
                    onValueChange = { module = it },
                    label = { Text("Module / Topic (e.g. Module 3: Consensus)") },
                    modifier = Modifier.fillMaxWidth().testTag("upload_note_module_input"),
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title (e.g. Paxos & Raft Cheatsheet)") },
                    modifier = Modifier.fillMaxWidth().testTag("upload_note_title_input"),
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

                Spacer(modifier = Modifier.height(14.dp))

                // Real File Picker Section
                Text(
                    "ATTACHMENT FILE (PDF, PNG, DOCX, WEBM, TEX)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = palette.accentOrange
                )

                Spacer(modifier = Modifier.height(6.dp))

                // File Selection Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, palette.border, RectangleShape)
                        .background(palette.background)
                        .padding(12.dp)
                ) {
                    Column {
                        if (selectedFileUri != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = BrutalOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = selectedFileName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = palette.textPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Ready to upload to storage",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = palette.textSecondary
                                        )
                                    }
                                }
                                BrutalButton(
                                    onClick = { filePickerLauncher.launch("*/*") },
                                    backgroundColor = palette.surface,
                                    contentColor = palette.textPrimary,
                                    borderWidth = 1.5.dp,
                                    shadowOffset = 2.dp
                                ) {
                                    Text("CHANGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black))
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No file chosen yet",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                BrutalButton(
                                    onClick = { filePickerLauncher.launch("*/*") },
                                    backgroundColor = BrutalNeonYellow,
                                    contentColor = BrutalBlack,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("choose_file_button"),
                                    borderWidth = 2.dp,
                                    shadowOffset = 3.dp
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AttachFile,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "SELECT FILE (PDF / PNG / DOCX / WEBM / TEX)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (localError != null || uploadError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, palette.border, RectangleShape)
                            .background(Color(0xFFFFEBEE))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = localError ?: uploadError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFC62828)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BrutalButton(
                    onClick = {
                        if (title.isBlank() || subject.isBlank()) {
                            localError = "Please enter both Subject and Note Title."
                            return@BrutalButton
                        }
                        val uri = selectedFileUri
                        if (uri == null) {
                            localError = "Please select a file to upload (PDF, PNG, DOCX, WEBM, TEX, etc.)."
                            return@BrutalButton
                        }
                        onSubmit(subject, module, title, uri, selectedFileName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_upload_notes_button"),
                    backgroundColor = if (isUploading) BrutalNeonYellow else BrutalOrange,
                    contentColor = if (isUploading) BrutalBlack else BrutalWhite,
                    borderWidth = 2.dp,
                    shadowOffset = 4.dp
                ) {
                    if (isUploading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = BrutalBlack
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "UPLOADING TO STORAGE...",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "PUBLISH COMMUNITY NOTE",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailDialog(
    note: SharedNote,
    downloadStatus: DownloadStatus = DownloadStatus.Idle,
    onDownload: () -> Unit = {},
    onOpen: () -> Unit = {},
    onDismiss: () -> Unit,
    onUpvote: () -> Unit
) {
    val context = LocalContext.current
    val palette = LocalBrutalPalette.current
    val isRemoteUrl = note.downloadUrl.startsWith("http://") || note.downloadUrl.startsWith("https://")

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, palette.border, RectangleShape),
            shape = RectangleShape,
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrutalTag(
                        text = note.subjectName,
                        backgroundColor = CatAcademic,
                        textColor = BrutalWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary
                )

                if (note.moduleTitle.isNotBlank()) {
                    Text(
                        text = note.moduleTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = palette.accentOrange
                    )
                }

                Text(
                    text = "Uploaded by ${note.uploaderName} • ${DateUtils.getRelativeTimeSpanString(note.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = palette.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isRemoteUrl) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, palette.border, RectangleShape)
                            .background(palette.background, RectangleShape)
                            .padding(14.dp)
                    ) {
                        Text(
                            "ATTACHED FILE RESOURCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = palette.accentOrange
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Cloud storage file available for this note. Tap below to download or view.",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        NoteDownloadProgressIndicator(
                            status = downloadStatus,
                            onDownload = onDownload,
                            onOpen = onOpen,
                            testTagPrefix = "dialog_note_${note.id}"
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, palette.border, RectangleShape)
                            .background(palette.background, RectangleShape)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = note.downloadUrl,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = palette.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrutalButton(
                        onClick = onUpvote,
                        backgroundColor = BrutalNeonYellow,
                        contentColor = BrutalBlack
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "${note.upvotes} UPVOTES",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    BrutalButton(
                        onClick = onDismiss,
                        backgroundColor = palette.surface,
                        contentColor = palette.textPrimary
                    ) {
                        Text("CLOSE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black))
                    }
                }
            }
        }
    }
}
