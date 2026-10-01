package com.example.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Module
import com.example.data.Note
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.components.NoteDownloadProgressIndicator
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.CatEvent
import com.example.ui.theme.LocalBrutalPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: Int,
    viewModel: NotesViewModel,
    onBack: () -> Unit
) {
    val palette = LocalBrutalPalette.current
    val subject by viewModel.getSubjectById(subjectId).collectAsStateWithLifecycle()
    val modules by viewModel.getModulesForSubject(subjectId).collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        subject?.name?.uppercase() ?: "SUBJECT SYLLABUS",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = palette.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.surface
                ),
                modifier = Modifier.border(2.dp, palette.border, RectangleShape)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.background)
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(modules, key = { it.id }) { module ->
                ModuleSection(module, viewModel)
            }
        }
    }
}

@Composable
fun ModuleSection(module: Module, viewModel: NotesViewModel) {
    val palette = LocalBrutalPalette.current
    val notes by viewModel.getNotesForModule(module.id).collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
                Text(
                    "MODULE ${module.orderIndex}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    ),
                    color = palette.accentOrange
                )
                val statusColor = if (module.progressStatus == "Studied") CatAcademic else BrutalNeonYellow
                BrutalTag(
                    text = module.progressStatus,
                    backgroundColor = statusColor,
                    textColor = if (module.progressStatus == "Studied") BrutalWhite else BrutalBlack
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                module.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = palette.textPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            notes.forEach { note ->
                val noteKey = "note_${note.id}"
                val status = downloadStates[noteKey] ?: DownloadStatus.Idle
                NoteItem(
                    note = note,
                    downloadStatus = status,
                    onDownload = {
                        viewModel.downloadNoteFile(
                            context = context,
                            noteKey = noteKey,
                            downloadUrl = note.content,
                            title = note.title,
                            autoOpen = true
                        )
                    },
                    onOpen = {
                        if (status is DownloadStatus.Completed) {
                            viewModel.openDownloadedFile(context, status.fileUri, status.localPath)
                        }
                    },
                    onToggleBookmark = { viewModel.toggleBookmark(note) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    downloadStatus: DownloadStatus = DownloadStatus.Idle,
    onDownload: () -> Unit = {},
    onOpen: () -> Unit = {},
    onToggleBookmark: () -> Unit
) {
    val palette = LocalBrutalPalette.current
    val isRemoteUrl = note.content.startsWith("http://") || note.content.startsWith("https://")

    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        borderWidth = 2.dp,
        shadowOffset = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        note.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    Text(
                        "TYPE: ${note.fileType.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.textSecondary
                    )
                }

                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (note.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (note.isBookmarked) BrutalOrange else palette.textSecondary
                    )
                }
            }

            if (isRemoteUrl) {
                Spacer(modifier = Modifier.height(8.dp))
                NoteDownloadProgressIndicator(
                    status = downloadStatus,
                    onDownload = onDownload,
                    onOpen = onOpen,
                    testTagPrefix = "syllabus_note_${note.id}"
                )
            }
        }
    }
}
