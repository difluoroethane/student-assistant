package com.example.ui.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.CommunityNote
import com.example.data.Module
import com.example.data.Note
import com.example.data.SharedNote
import com.example.data.Subject
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

sealed class DownloadStatus {
    object Idle : DownloadStatus()
    data class Downloading(val progress: Float) : DownloadStatus() // 0.0f..1.0f or -1f if indeterminate
    data class Completed(val fileUri: Uri, val localPath: String) : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
}

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(
    private val repository: AppRepository,
    private val dao: com.example.data.AppDao = repository.dao
) : ViewModel() {
    private val _department = MutableStateFlow("")
    private val _semester = MutableStateFlow("")

    val subjects: StateFlow<List<Subject>> = _department.flatMapLatest { dept ->
        _semester.flatMapLatest { sem ->
            if (dept.isNotEmpty() && sem.isNotEmpty()) repository.getSubjects(dept, sem)
            else flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sharedNotes: StateFlow<List<SharedNote>> = repository.getSharedNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError.asStateFlow()

    private val _notesTab = MutableStateFlow(0) // 0: My Subject Syllabus, 1: Community Shared Notes
    val notesTab: StateFlow<Int> = _notesTab.asStateFlow()

    private val _downloadStates = MutableStateFlow<Map<String, DownloadStatus>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadStatus>> = _downloadStates.asStateFlow()

    init {
        viewModelScope.launch {
            repository.syncWithCloud()
        }
    }

    fun setNotesTab(tabIndex: Int) {
        _notesTab.value = tabIndex
    }

    fun setParams(department: String, semester: String) {
        _department.value = department
        _semester.value = semester
    }

    fun getModulesForSubject(subjectId: Int): StateFlow<List<Module>> {
        return repository.getModulesForSubject(subjectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getNotesForModule(moduleId: Int): StateFlow<List<Note>> {
        return repository.getNotesForModule(moduleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun searchNotes(query: String): StateFlow<List<Note>> {
        return repository.searchNotes(query)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getSubjectById(subjectId: Int): StateFlow<Subject?> {
        return repository.getSubjectById(subjectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun toggleBookmark(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isBookmarked = !note.isBookmarked))
        }
    }

    fun updateModuleProgress(module: Module, status: String) {
        viewModelScope.launch {
            repository.updateModule(module.copy(progressStatus = status))
        }
    }

    fun upvoteSharedNote(noteId: String, userId: String) {
        viewModelScope.launch {
            repository.upvoteCommunityNote(noteId, userId)
        }
    }

    fun uploadSharedNote(
        subjectName: String,
        moduleTitle: String,
        title: String,
        content: String,
        uploaderName: String
    ) {
        val noteId = UUID.randomUUID().toString()
        val departmentVal = if (_department.value.isNotBlank()) _department.value else "Engineering"
        val note = SharedNote(
            id = noteId,
            subject = subjectName.trim(),
            moduleTitle = moduleTitle.trim(),
            title = title.trim(),
            downloadUrl = content.trim(),
            author = uploaderName,
            upvotedBy = listOf(uploaderName),
            timestamp = System.currentTimeMillis(),
            authorUsername = uploaderName,
            authorDept = departmentVal,
            department = departmentVal
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // STEP 1: Save to local Room database immediately
                val localNote = Note(
                    id = 0,
                    moduleId = 0,
                    title = title.trim(),
                    subject = subjectName.trim(),
                    fileType = "Summary",
                    content = content.trim(),
                    uploadedDate = System.currentTimeMillis(),
                    isBookmarked = false,
                    authorUsername = uploaderName,
                    department = departmentVal,
                    downloadUrl = content.trim(),
                    timestamp = System.currentTimeMillis()
                )
                dao.insertNote(localNote)
                dao.insertCommunityNote(note)

                // STEP 2: Push to Cloud Firestore
                repository.uploadCommunityNote(note)
            } catch (e: Exception) {
                android.util.Log.e("NotesViewModel", "uploadSharedNote failed: ${e.message}", e)
            }
        }
    }

    fun uploadNoteWithFile(
        subjectName: String,
        moduleTitle: String,
        title: String,
        fileUri: Uri,
        fileName: String,
        uploaderName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isUploading.value = true
            _uploadError.value = null
            try {
                val noteId = UUID.randomUUID().toString()
                val departmentVal = if (_department.value.isNotBlank()) _department.value else "Engineering"
                val ext = fileName.substringAfterLast('.', "Document").uppercase()

                // STEP 1: Save the Post/Note to the LOCAL Room Database immediately (dao.insert...)
                // This guarantees it appears in the local SQLite database immediately.
                val initialCommunityNote = CommunityNote(
                    id = noteId,
                    subject = subjectName.trim(),
                    moduleTitle = moduleTitle.trim(),
                    title = title.trim(),
                    author = uploaderName,
                    downloadUrl = "",
                    timestamp = System.currentTimeMillis(),
                    upvotedBy = listOf(uploaderName),
                    authorUsername = uploaderName,
                    authorDept = departmentVal,
                    department = departmentVal
                )
                dao.insertCommunityNote(initialCommunityNote)

                val initialLocalNote = Note(
                    id = 0,
                    moduleId = 0,
                    title = title.trim(),
                    subject = subjectName.trim(),
                    fileType = ext,
                    content = fileName,
                    uploadedDate = System.currentTimeMillis(),
                    isBookmarked = false,
                    authorUsername = uploaderName,
                    department = departmentVal,
                    downloadUrl = "",
                    timestamp = System.currentTimeMillis()
                )
                val localNoteId = dao.insertNote(initialLocalNote)

                // Dismiss dialog and notify UI immediately after local save
                withContext(Dispatchers.Main) {
                    _isUploading.value = false
                    onSuccess()
                }

                // STEP 2: If a file/image Uri is attached, call uploadFileToStorage(uri). Wait for the downloadUrl.
                val downloadUrl = try {
                    repository.uploadNoteFile(fileUri, fileName)
                } catch (e: Exception) {
                    android.util.Log.e("NotesViewModel", "Storage upload failed: ${e.message}", e)
                    ""
                }

                // STEP 3: Update the local Room database with the new downloadUrl.
                val finalCommunityNote = if (downloadUrl.isNotBlank()) {
                    val updatedCommNote = initialCommunityNote.copy(downloadUrl = downloadUrl)
                    dao.insertCommunityNote(updatedCommNote)
                    updatedCommNote
                } else {
                    initialCommunityNote
                }

                if (downloadUrl.isNotBlank()) {
                    val updatedLocalNote = initialLocalNote.copy(
                        id = localNoteId.toInt(),
                        content = downloadUrl,
                        downloadUrl = downloadUrl
                    )
                    dao.insertNote(updatedLocalNote)
                }

                // STEP 4: Call uploadCommunityNote() to push the final object to Cloud Firestore.
                repository.uploadCommunityNote(finalCommunityNote)
            } catch (e: Exception) {
                android.util.Log.e("NotesViewModel", "Error in uploadNoteWithFile pipeline: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _isUploading.value = false
                    onSuccess()
                }
            }
        }
    }

    fun downloadNoteFile(
        context: Context,
        noteKey: String,
        downloadUrl: String,
        title: String,
        autoOpen: Boolean = false
    ) {
        if (downloadUrl.isBlank() || (!downloadUrl.startsWith("http://") && !downloadUrl.startsWith("https://"))) {
            _downloadStates.update { it + (noteKey to DownloadStatus.Error("Invalid download URL")) }
            return
        }

        val currentStatus = _downloadStates.value[noteKey]
        if (currentStatus is DownloadStatus.Downloading) {
            return
        }

        _downloadStates.update { it + (noteKey to DownloadStatus.Downloading(0f)) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }

                val ext = when {
                    downloadUrl.contains(".pdf", ignoreCase = true) -> "pdf"
                    downloadUrl.contains(".png", ignoreCase = true) -> "png"
                    downloadUrl.contains(".docx", ignoreCase = true) -> "docx"
                    downloadUrl.contains(".doc", ignoreCase = true) -> "doc"
                    downloadUrl.contains(".webm", ignoreCase = true) -> "webm"
                    downloadUrl.contains(".tex", ignoreCase = true) -> "tex"
                    downloadUrl.contains(".jpg", ignoreCase = true) || downloadUrl.contains(".jpeg", ignoreCase = true) -> "jpg"
                    else -> "pdf"
                }
                val safeTitle = title.replace("[^a-zA-Z0-9_-]".toRegex(), "_").take(32).ifBlank { "note_file" }
                val targetFile = File(downloadsDir, "${safeTitle}_${noteKey.takeLast(6)}.$ext")

                val url = URL(downloadUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 30000
                    instanceFollowRedirects = true
                    requestMethod = "GET"
                }
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    throw IOException("Server responded with HTTP $responseCode")
                }

                val totalBytes = connection.contentLengthLong
                var bytesRead = 0L
                val inputStream = BufferedInputStream(connection.inputStream)
                val outputStream = FileOutputStream(targetFile)
                val buffer = ByteArray(8192)
                var count: Int
                var lastUpdate = 0L

                while (inputStream.read(buffer).also { count = it } != -1) {
                    outputStream.write(buffer, 0, count)
                    bytesRead += count
                    val now = System.currentTimeMillis()
                    if (now - lastUpdate > 75) {
                        lastUpdate = now
                        val progress = if (totalBytes > 0) (bytesRead.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else -1f
                        _downloadStates.update { it + (noteKey to DownloadStatus.Downloading(progress)) }
                    }
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                connection.disconnect()

                val fileUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    targetFile
                )

                _downloadStates.update { it + (noteKey to DownloadStatus.Completed(fileUri, targetFile.absolutePath)) }

                if (autoOpen) {
                    withContext(Dispatchers.Main) {
                        openDownloadedFile(context, fileUri, targetFile.absolutePath)
                    }
                }
            } catch (e: Exception) {
                _downloadStates.update { it + (noteKey to DownloadStatus.Error(e.localizedMessage ?: "Download failed")) }
            }
        }
    }

    fun openDownloadedFile(context: Context, fileUri: Uri, localPath: String) {
        try {
            val extension = File(localPath).extension.lowercase()
            val mimeType = when (extension) {
                "pdf" -> "application/pdf"
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                "doc" -> "application/msword"
                "webm" -> "video/webm"
                "tex" -> "text/x-tex"
                else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open file with..."))
        } catch (e: Exception) {
            try {
                val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(genericIntent, "Open file with..."))
            } catch (ignored: Exception) {}
        }
    }
}
