package com.example.data

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class SyncResult(
    val success: Boolean,
    val isOffline: Boolean,
    val message: String,
    val syncedAnnouncementsCount: Int = 0,
    val syncedNotesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

interface AppRepository {
    val dao: AppDao
    fun getAllAnnouncements(): Flow<List<Announcement>>
    suspend fun insertAnnouncement(announcement: Announcement)
    fun getSubjects(department: String, semester: String): Flow<List<Subject>>
    fun getSubjectById(id: Int): Flow<Subject?>
    suspend fun insertSubject(subject: Subject): Long
    fun getModulesForSubject(subjectId: Int): Flow<List<Module>>
    suspend fun insertModule(module: Module): Long
    suspend fun updateModule(module: Module)
    fun getNotesForModule(moduleId: Int): Flow<List<Note>>
    fun getBookmarkedNotes(): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getNoteById(id: Int): Flow<Note?>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    fun getAllAssignments(): Flow<List<Assignment>>
    suspend fun insertAssignment(assignment: Assignment)
    suspend fun updateAssignment(assignment: Assignment)
    fun getAllExams(): Flow<List<Exam>>
    suspend fun insertExam(exam: Exam)
    fun getAllTimetableEntries(): Flow<List<TimetableEntry>>
    suspend fun insertTimetableEntry(entry: TimetableEntry)
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun insertUserProfile(profile: UserProfile)
    suspend fun updateUsername(username: String)

    // Announcements
    suspend fun postAnnouncement(title: String, description: String, category: String, pinned: Boolean): Announcement

    // Cloud Data Synchronization
    suspend fun syncWithCloud(): SyncResult
    fun getAllCommunityNotes(): Flow<List<CommunityNote>>
    suspend fun uploadCommunityNote(note: CommunityNote)
    suspend fun uploadNoteFile(fileUri: Uri, fileName: String): String
    suspend fun uploadForumImage(imageUri: Uri): String
    suspend fun upvoteCommunityNote(noteId: String, userId: String)
    fun getCloudAnnouncements(): Flow<List<CloudAnnouncement>>

    // Legacy helpers for existing screens
    fun getSharedNotes(): Flow<List<CommunityNote>> = getAllCommunityNotes()
    fun uploadSharedNote(note: CommunityNote)
    fun upvoteSharedNote(noteId: String, userId: String)

    // Forum
    fun getForumPosts(tag: String? = null): Flow<List<ForumPost>>
    fun createForumPost(post: ForumPost)
    suspend fun upvoteForumPost(postId: String, userId: String)
    fun getForumComments(postId: String): Flow<List<ForumComment>>
    fun addForumComment(comment: ForumComment)
}

class LocalAppRepository(
    override val dao: AppDao,
    private val firestoreService: FirestoreService
) : AppRepository {
    private val repoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        repoScope.launch {
            firestoreService.communityNotes.collect { notes ->
                if (notes.isNotEmpty()) {
                    try {
                        dao.insertCommunityNotes(notes)
                        notes.forEach { cNote ->
                            dao.insertNote(
                                Note(
                                    id = 0,
                                    title = cNote.title,
                                    subject = cNote.subject,
                                    fileType = "PDF",
                                    content = cNote.downloadUrl,
                                    uploadedDate = cNote.timestamp,
                                    isBookmarked = false,
                                    authorUsername = cNote.authorUsername,
                                    department = cNote.department,
                                    downloadUrl = cNote.downloadUrl,
                                    timestamp = cNote.timestamp
                                )
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("LocalAppRepository", "Failed to cache community notes: ${e.message}")
                    }
                }
            }
        }
        repoScope.launch {
            firestoreService.forumPosts.collect { posts ->
                if (posts.isNotEmpty()) {
                    try {
                        dao.insertForumPosts(posts)
                    } catch (e: Exception) {
                        android.util.Log.w("LocalAppRepository", "Failed to cache forum posts: ${e.message}")
                    }
                }
            }
        }
        repoScope.launch {
            firestoreService.cloudAnnouncements.collect { ann ->
                if (ann.isNotEmpty()) {
                    try {
                        dao.insertCloudAnnouncements(ann)
                    } catch (e: Exception) {
                        android.util.Log.w("LocalAppRepository", "Failed to cache cloud announcements: ${e.message}")
                    }
                }
            }
        }
    }

    override fun getAllAnnouncements() = dao.getAllAnnouncements()
    override suspend fun insertAnnouncement(announcement: Announcement) = dao.insertAnnouncement(announcement)
    override fun getSubjects(department: String, semester: String) = dao.getSubjects(department, semester)
    override fun getSubjectById(id: Int) = dao.getSubjectById(id)
    override suspend fun insertSubject(subject: Subject) = dao.insertSubject(subject)
    override fun getModulesForSubject(subjectId: Int) = dao.getModulesForSubject(subjectId)
    override suspend fun insertModule(module: Module) = dao.insertModule(module)
    override suspend fun updateModule(module: Module) = dao.updateModule(module)
    override fun getNotesForModule(moduleId: Int) = dao.getNotesForModule(moduleId)
    override fun getBookmarkedNotes() = dao.getBookmarkedNotes()
    override fun searchNotes(query: String) = dao.searchNotes(query)
    override fun getNoteById(id: Int) = dao.getNoteById(id)
    override suspend fun insertNote(note: Note) = dao.insertNote(note)
    override suspend fun updateNote(note: Note) = dao.updateNote(note)
    override fun getAllAssignments() = dao.getAllAssignments()
    override suspend fun insertAssignment(assignment: Assignment) = dao.insertAssignment(assignment)
    override suspend fun updateAssignment(assignment: Assignment) = dao.updateAssignment(assignment)
    override fun getAllExams() = dao.getAllExams()
    override suspend fun insertExam(exam: Exam) = dao.insertExam(exam)
    override fun getAllTimetableEntries() = dao.getAllTimetableEntries()
    override suspend fun insertTimetableEntry(entry: TimetableEntry) = dao.insertTimetableEntry(entry)
    override fun getUserProfile() = dao.getUserProfile()
    override suspend fun insertUserProfile(profile: UserProfile) {
        dao.insertUserProfile(profile)
        firestoreService.syncUserProfileSilently(profile)
    }

    override suspend fun updateUsername(username: String) {
        val current = dao.getUserProfile().firstOrNull()
        if (current != null) {
            val updated = current.copy(username = username)
            dao.insertUserProfile(updated)
            firestoreService.syncUserProfileSilently(updated)
        }
    }

    // Experiment 4: Cloud Data Synchronization
    override suspend fun syncWithCloud(): SyncResult = withContext(Dispatchers.IO) {
        try {
            firestoreService.ensureAuthenticated()
            // Fetch from Cloud Firestore
            val cloudAnnouncements = firestoreService.fetchCloudAnnouncements()
            val communityNotes = firestoreService.fetchCommunityNotes()
            val forumPosts = firestoreService.fetchForumPosts()

            // Save to local Room database cache
            if (cloudAnnouncements.isNotEmpty()) {
                dao.insertCloudAnnouncements(cloudAnnouncements)
            }
            if (communityNotes.isNotEmpty()) {
                dao.insertCommunityNotes(communityNotes)
                communityNotes.forEach { cNote ->
                    dao.insertNote(
                        Note(
                            id = 0,
                            title = cNote.title,
                            subject = cNote.subject,
                            fileType = "PDF",
                            content = cNote.downloadUrl,
                            uploadedDate = cNote.timestamp,
                            isBookmarked = false,
                            authorUsername = cNote.authorUsername,
                            department = cNote.department,
                            downloadUrl = cNote.downloadUrl,
                            timestamp = cNote.timestamp
                        )
                    )
                }
            }
            if (forumPosts.isNotEmpty()) {
                dao.insertForumPosts(forumPosts)
            }

            SyncResult(
                success = true,
                isOffline = false,
                message = "Up to date",
                syncedAnnouncementsCount = cloudAnnouncements.size,
                syncedNotesCount = communityNotes.size
            )
        } catch (e: Exception) {
            // Offline fallback: preserve Room database records seamlessly
            SyncResult(
                success = true,
                isOffline = true,
                message = "Offline: Showing saved data"
            )
        }
    }

    override suspend fun postAnnouncement(
        title: String,
        description: String,
        category: String,
        pinned: Boolean
    ): Announcement = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val annId = UUID.randomUUID().toString()
        val localAnn = Announcement(
            title = title,
            description = description,
            category = category,
            timestamp = now,
            pinned = pinned
        )
        dao.insertAnnouncement(localAnn)

        val cloudAnn = CloudAnnouncement(
            id = annId,
            title = title,
            description = description,
            category = category,
            timestamp = now,
            pinned = pinned
        )
        dao.insertCloudAnnouncement(cloudAnn)
        firestoreService.uploadAnnouncement(cloudAnn)
        localAnn
    }

    override fun getAllCommunityNotes(): Flow<List<CommunityNote>> =
        combine(
            dao.getAllCommunityNotes(),
            firestoreService.communityNotes
        ) { local, remote ->
            val map = linkedMapOf<String, CommunityNote>()
            local.forEach { map[it.id] = it }
            remote.forEach { map[it.id] = it }
            map.values.sortedByDescending { it.upvotedBy.size }
        }

    override suspend fun uploadCommunityNote(note: CommunityNote) {
        val noteId = if (note.id.isNotBlank()) note.id else UUID.randomUUID().toString()
        val completeNote = note.copy(id = noteId)
        // Save locally to Room (both community_notes and notes tables)
        dao.insertCommunityNote(completeNote)
        dao.insertNote(
            Note(
                id = 0,
                title = completeNote.title,
                subject = completeNote.subject,
                fileType = "PDF",
                content = completeNote.downloadUrl,
                uploadedDate = completeNote.timestamp,
                isBookmarked = false,
                authorUsername = completeNote.authorUsername,
                department = completeNote.department,
                downloadUrl = completeNote.downloadUrl,
                timestamp = completeNote.timestamp
            )
        )
        // Sync remotely to Firestore
        firestoreService.uploadCommunityNote(completeNote)
    }

    override suspend fun uploadNoteFile(fileUri: Uri, fileName: String): String =
        withContext(Dispatchers.IO) {
            firestoreService.uploadFileToStorage(fileUri, fileName)
        }

    override suspend fun uploadForumImage(imageUri: Uri): String =
        withContext(Dispatchers.IO) {
            firestoreService.uploadForumImage(imageUri)
        }

    override suspend fun upvoteCommunityNote(noteId: String, userId: String) {
        val local = dao.getCommunityNoteById(noteId)
        if (local != null) {
            val list = local.upvotedBy.toMutableList()
            if (list.contains(userId)) list.remove(userId) else list.add(userId)
            dao.insertCommunityNote(local.copy(upvotedBy = list))
        }
        firestoreService.toggleUpvoteCommunityNote(noteId, userId)
    }

    override fun getCloudAnnouncements(): Flow<List<CloudAnnouncement>> =
        combine(
            dao.getAllCloudAnnouncements(),
            firestoreService.cloudAnnouncements
        ) { local, remote ->
            val map = linkedMapOf<String, CloudAnnouncement>()
            local.forEach { map[it.id] = it }
            remote.forEach { map[it.id] = it }
            map.values.sortedByDescending { it.timestamp }
        }

    override fun uploadSharedNote(note: CommunityNote) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                uploadCommunityNote(note)
            } catch (e: Exception) {
                android.util.Log.e("LocalAppRepository", "uploadSharedNote failed: ${e.message}", e)
            }
        }
    }

    override fun upvoteSharedNote(noteId: String, userId: String) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                upvoteCommunityNote(noteId, userId)
            } catch (e: Exception) {
                android.util.Log.e("LocalAppRepository", "upvoteSharedNote failed: ${e.message}", e)
            }
        }
    }

    // Forum
    override fun getForumPosts(tag: String?): Flow<List<ForumPost>> =
        combine(
            if (tag == null || tag == "All") dao.getAllForumPosts() else dao.getForumPostsByTag(tag),
            firestoreService.getPostsByTag(tag)
        ) { local, remote ->
            val map = linkedMapOf<String, ForumPost>()
            local.forEach { map[it.id] = it }
            remote.forEach { map[it.id] = it }
            val list = map.values.toList()
            when (tag) {
                null, "All" -> list.sortedByDescending { it.timestamp }
                "Hot" -> list.sortedByDescending { it.upvotes }
                else -> list.filter { it.tag.equals(tag, ignoreCase = true) }.sortedByDescending { it.timestamp }
            }
        }

    override fun createForumPost(post: ForumPost) {
        val postId = if (post.id.isNotBlank()) post.id else UUID.randomUUID().toString()
        val completePost = post.copy(id = postId)
        // Save locally to Room database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.insertForumPost(completePost)
            } catch (e: Exception) {
                android.util.Log.e("LocalAppRepository", "Error saving forum post locally: ${e.message}", e)
            }
        }
        // Push remotely to Firestore
        firestoreService.createPost(completePost)
    }

    override suspend fun upvoteForumPost(postId: String, userId: String) {
        val local = dao.getForumPostById(postId)
        if (local != null) {
            val list = local.upvotedBy.toMutableList()
            if (list.contains(userId)) list.remove(userId) else list.add(userId)
            dao.insertForumPost(local.copy(upvotedBy = list))
        }
        firestoreService.toggleUpvotePost(postId, userId)
    }
    override fun getForumComments(postId: String): Flow<List<ForumComment>> = firestoreService.getCommentsForPost(postId)
    override fun addForumComment(comment: ForumComment) = firestoreService.addComment(comment)
}
