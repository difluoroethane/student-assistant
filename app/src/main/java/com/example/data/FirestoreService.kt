package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.util.UUID

class FirestoreService(
    private val context: Context,
    private val dao: AppDao
) {
    private val TAG = "FirestoreService"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _forumPosts = MutableStateFlow<List<ForumPost>>(emptyList())
    val forumPosts: Flow<List<ForumPost>> = _forumPosts.asStateFlow()

    private val _forumComments = MutableStateFlow<Map<String, List<ForumComment>>>(emptyMap())

    private val _communityNotes = MutableStateFlow<List<CommunityNote>>(emptyList())
    val communityNotes: Flow<List<CommunityNote>> = _communityNotes.asStateFlow()
    val sharedNotes: Flow<List<CommunityNote>> = _communityNotes.asStateFlow()

    private val _cloudAnnouncements = MutableStateFlow<List<CloudAnnouncement>>(emptyList())
    val cloudAnnouncements: Flow<List<CloudAnnouncement>> = _cloudAnnouncements.asStateFlow()

    private var forumPostsListener: ListenerRegistration? = null
    private var forumCommentsListener: ListenerRegistration? = null
    private var communityNotesListener: ListenerRegistration? = null
    private var announcementsListener: ListenerRegistration? = null

    fun getFirestoreInstance(): FirebaseFirestore = firestore

    init {
        // Authenticate anonymously so security rules permitting authenticated users pass immediately
        scope.launch {
            ensureAuthenticated()
            setupFirestoreListeners()
        }
    }

    suspend fun ensureAuthenticated() {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
                Log.d(TAG, "Anonymous auth succeeded: ${auth.currentUser?.uid}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Anonymous auth note: ${e.message}")
        }
    }

    @Synchronized
    fun setupFirestoreListeners() {
        // 1. Forum Posts Listener
        try {
            forumPostsListener?.remove()
            forumPostsListener = firestore.collection("forum_posts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "forum_posts listener warning: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val posts = snapshot.documents.mapNotNull { parseForumPost(it) }
                            .sortedByDescending { it.timestamp }
                        _forumPosts.value = posts
                        scope.launch {
                            try {
                                if (posts.isNotEmpty()) {
                                    dao.insertForumPosts(posts)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error caching forum_posts in Room: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up forum_posts listener: ${e.message}", e)
        }

        // 2. Forum Comments Listener
        try {
            forumCommentsListener?.remove()
            forumCommentsListener = firestore.collection("forum_comments")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "forum_comments listener warning: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val map = mutableMapOf<String, MutableList<ForumComment>>()
                        snapshot.documents.forEach { doc ->
                            parseForumComment(doc)?.let { comment ->
                                map.getOrPut(comment.postId) { mutableListOf() }.add(comment)
                            }
                        }
                        map.values.forEach { it.sortBy { c -> c.timestamp } }
                        _forumComments.value = map
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up forum_comments listener: ${e.message}", e)
        }

        // 3. Community Notes Listener
        try {
            communityNotesListener?.remove()
            communityNotesListener = firestore.collection("community_notes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "community_notes listener warning: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val notes = snapshot.documents.mapNotNull { parseCommunityNote(it) }
                            .sortedByDescending { it.upvotedBy.size }
                        _communityNotes.value = notes
                        scope.launch {
                            try {
                                if (notes.isNotEmpty()) {
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
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error caching community_notes in Room: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up community_notes listener: ${e.message}", e)
        }

        // 4. Cloud Announcements Listener
        try {
            announcementsListener?.remove()
            announcementsListener = firestore.collection("cloud_announcements")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "cloud_announcements listener warning: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { parseCloudAnnouncement(it) }
                            .sortedByDescending { it.timestamp }
                        _cloudAnnouncements.value = items
                        scope.launch {
                            try {
                                if (items.isNotEmpty()) {
                                    dao.insertCloudAnnouncements(items)
                                    items.forEach { ann ->
                                        dao.insertAnnouncement(
                                            Announcement(
                                                id = 0,
                                                title = ann.title,
                                                description = ann.description,
                                                category = ann.category,
                                                timestamp = ann.timestamp,
                                                pinned = ann.pinned
                                            )
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error caching cloud_announcements in Room: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up cloud_announcements listener: ${e.message}", e)
        }
    }

    // Safe parser helpers handling Timestamps, Strings, and Numbers
    private fun extractTimestamp(doc: DocumentSnapshot, field: String = "timestamp"): Long {
        return when (val raw = doc.get(field)) {
            is Number -> raw.toLong()
            is com.google.firebase.Timestamp -> raw.toDate().time
            is String -> raw.toLongOrNull() ?: System.currentTimeMillis()
            else -> System.currentTimeMillis()
        }
    }

    private fun extractUpvotedBy(doc: DocumentSnapshot): List<String> {
        val raw = doc.get("upvotedBy")
        if (raw is List<*>) {
            return raw.mapNotNull { it?.toString() }
        }
        val upvotesNum = (doc.get("upvotes") as? Number)?.toInt() ?: 0
        return if (upvotesNum > 0) List(upvotesNum) { "user_$it" } else emptyList()
    }

    private fun parseForumPost(doc: DocumentSnapshot): ForumPost? {
        return try {
            val upvotedByList = extractUpvotedBy(doc)
            ForumPost(
                id = doc.id,
                authorUsername = doc.getString("authorUsername") ?: doc.getString("author") ?: "@AnonCowboy",
                authorDept = doc.getString("authorDept") ?: "Engineering",
                content = doc.getString("content") ?: "",
                tag = doc.getString("tag") ?: "Campus Life",
                upvotedBy = upvotedByList,
                commentCount = (doc.get("commentCount") as? Number)?.toInt() ?: 0,
                imageUrl = doc.getString("imageUrl"),
                timestamp = extractTimestamp(doc)
            )
        } catch (e: Exception) {
            Log.d(TAG, "Failed parsing forum post ${doc.id}: ${e.message}")
            null
        }
    }

    private fun parseForumComment(doc: DocumentSnapshot): ForumComment? {
        return try {
            ForumComment(
                id = doc.id,
                postId = doc.getString("postId") ?: "",
                authorUsername = doc.getString("authorUsername") ?: "@AnonCowboy",
                content = doc.getString("content") ?: "",
                timestamp = extractTimestamp(doc)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseCommunityNote(doc: DocumentSnapshot): CommunityNote? {
        return try {
            val upvotedByList = extractUpvotedBy(doc)
            val downloadUrl = doc.getString("downloadUrl")
                ?: doc.getString("content")
                ?: doc.getString("fileUrl")
                ?: ""
            val authorUser = doc.getString("authorUsername")
                ?: doc.getString("author")
                ?: doc.getString("uploaderName")
                ?: "@AnonCowboy"
            val authorDepartment = doc.getString("authorDept") ?: "Engineering"
            CommunityNote(
                id = doc.id,
                subject = doc.getString("subject") ?: doc.getString("subjectName") ?: "General",
                moduleTitle = doc.getString("moduleTitle") ?: "Module 1",
                title = doc.getString("title") ?: doc.getString("noteTitle") ?: "Untitled Note",
                author = authorUser,
                downloadUrl = downloadUrl,
                timestamp = extractTimestamp(doc),
                upvotedBy = upvotedByList,
                authorUsername = authorUser,
                authorDept = authorDepartment
            )
        } catch (e: Exception) {
            Log.d(TAG, "Failed parsing community note ${doc.id}: ${e.message}")
            null
        }
    }

    private fun parseCloudAnnouncement(doc: DocumentSnapshot): CloudAnnouncement? {
        return try {
            CloudAnnouncement(
                id = doc.id,
                title = doc.getString("title") ?: "",
                description = doc.getString("description") ?: "",
                category = doc.getString("category") ?: "General",
                timestamp = extractTimestamp(doc),
                pinned = doc.getBoolean("pinned") ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    // Explicit synchronizations with timeout
    suspend fun fetchCloudAnnouncements(): List<CloudAnnouncement> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext _cloudAnnouncements.value
        try {
            val snapshot = withTimeoutOrNull(6000L) {
                fs.collection("cloud_announcements").get().await()
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val list = snapshot.documents.mapNotNull { parseCloudAnnouncement(it) }
                    .sortedByDescending { it.timestamp }
                _cloudAnnouncements.value = list
                list
            } else {
                _cloudAnnouncements.value
            }
        } catch (e: Exception) {
            Log.d(TAG, "fetchCloudAnnouncements fallback: ${e.message}")
            _cloudAnnouncements.value
        }
    }

    suspend fun fetchCommunityNotes(): List<CommunityNote> = withContext(Dispatchers.IO) {
        try {
            val snapshot = withTimeoutOrNull(6000L) {
                firestore.collection("community_notes").get().await()
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val list = snapshot.documents.mapNotNull { parseCommunityNote(it) }
                    .sortedByDescending { it.upvotedBy.size }
                _communityNotes.value = list
                list
            } else {
                _communityNotes.value
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchCommunityNotes error: ${e.message}", e)
            _communityNotes.value
        }
    }

    suspend fun fetchForumPosts(): List<ForumPost> = withContext(Dispatchers.IO) {
        try {
            val snapshot = withTimeoutOrNull(6000L) {
                firestore.collection("forum_posts").get().await()
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val list = snapshot.documents.mapNotNull { parseForumPost(it) }
                    .sortedByDescending { it.timestamp }
                _forumPosts.value = list
                list
            } else {
                _forumPosts.value
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchForumPosts error: ${e.message}", e)
            _forumPosts.value
        }
    }

    // Storage uploads
    suspend fun uploadFileToStorage(fileUri: Uri, originalFileName: String = ""): String = withContext(Dispatchers.IO) {
        ensureAuthenticated()
        try {
            val ref = firebaseStorage.reference.child("uploads/${UUID.randomUUID()}")
            ref.putFile(fileUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "Storage upload successful: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Storage upload failed: ${e.message}", e)
            ""
        }
    }

    suspend fun uploadForumImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        ensureAuthenticated()
        try {
            val ref = firebaseStorage.reference.child("uploads/${UUID.randomUUID()}.jpg")
            ref.putFile(imageUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "Forum image upload successful: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Forum image upload failed: ${e.message}", e)
            ""
        }
    }

    // Cloud Announcements Operations
    fun uploadAnnouncement(announcement: CloudAnnouncement) {
        val annId = if (announcement.id.isNotBlank()) announcement.id else UUID.randomUUID().toString()
        val completeAnn = announcement.copy(id = annId)

        val list = _cloudAnnouncements.value.toMutableList()
        if (list.none { it.id == annId }) {
            list.add(0, completeAnn)
            _cloudAnnouncements.value = list
        }

        scope.launch {
            // STEP 1: Explicit Local Room SQLite Insert
            try {
                dao.insertCloudAnnouncement(completeAnn)
                dao.insertAnnouncement(
                    Announcement(
                        id = 0,
                        title = completeAnn.title,
                        description = completeAnn.description,
                        category = completeAnn.category,
                        timestamp = completeAnn.timestamp,
                        pinned = completeAnn.pinned
                    )
                )
                Log.d(TAG, "Successfully inserted announcement $annId into local Room database")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting announcement $annId into local Room database: ${e.message}", e)
            }

            // STEP 2: Firebase Cloud Sync
            try {
                val map = hashMapOf(
                    "id" to completeAnn.id,
                    "title" to completeAnn.title,
                    "description" to completeAnn.description,
                    "category" to completeAnn.category,
                    "timestamp" to completeAnn.timestamp,
                    "pinned" to completeAnn.pinned
                )
                firestore.collection("cloud_announcements").document(annId).set(map, SetOptions.merge()).await()
                Log.d(TAG, "Successfully uploaded announcement $annId to Firestore")
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading announcement $annId: ${e.message}", e)
            }
        }
    }

    // Community Notes Operations
    fun uploadCommunityNote(note: CommunityNote) {
        val noteId = if (note.id.isNotBlank()) note.id else UUID.randomUUID().toString()
        val completeNote = note.copy(id = noteId)

        val list = _communityNotes.value.toMutableList()
        if (list.none { it.id == noteId }) {
            list.add(0, completeNote)
            _communityNotes.value = list
        }

        scope.launch {
            // STEP 1: Explicit Local Room SQLite Insert
            try {
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
                Log.d(TAG, "Successfully inserted community note ${completeNote.id} into local Room database")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting community note ${completeNote.id} into local Room database: ${e.message}", e)
            }

            // STEP 2: Firebase Cloud Sync
            try {
                val authorUser = if (completeNote.authorUsername.isNotBlank()) completeNote.authorUsername else completeNote.author
                val authorDept = if (completeNote.authorDept.isNotBlank()) completeNote.authorDept else "Engineering"
                val map = hashMapOf<String, Any>(
                    "id" to completeNote.id,
                    "authorUsername" to authorUser,
                    "author" to authorUser,
                    "uploaderName" to authorUser,
                    "title" to completeNote.title,
                    "noteTitle" to completeNote.title,
                    "subject" to completeNote.subject,
                    "subjectName" to completeNote.subject,
                    "moduleTitle" to completeNote.moduleTitle,
                    "downloadUrl" to completeNote.downloadUrl,
                    "fileUrl" to completeNote.downloadUrl,
                    "content" to completeNote.downloadUrl,
                    "authorDept" to authorDept,
                    "department" to authorDept,
                    "timestamp" to if (completeNote.timestamp != 0L) completeNote.timestamp else System.currentTimeMillis(),
                    "upvotedBy" to completeNote.upvotedBy,
                    "upvotes" to completeNote.upvotes
                )

                // Must use .collection("community_notes").document(note.id).set(map) to sync Room and Firestore
                firestore.collection("community_notes").document(completeNote.id).set(map, SetOptions.merge()).await()
                Log.d(TAG, "Successfully synced community note ${completeNote.id} to Firestore")
            } catch (e: Exception) {
                Log.e(TAG, "Error writing community note ${completeNote.id} to Firestore: ${e.message}", e)
            }
        }
    }

    fun toggleUpvoteCommunityNote(noteId: String, userId: String) {
        val note = _communityNotes.value.find { it.id == noteId } ?: return
        val currentList = note.upvotedBy.toMutableList()
        val isUpvoted = currentList.contains(userId)
        if (isUpvoted) {
            currentList.remove(userId)
        } else {
            currentList.add(userId)
        }
        val updatedNote = note.copy(upvotedBy = currentList)
        _communityNotes.value = _communityNotes.value.map { if (it.id == noteId) updatedNote else it }

        scope.launch {
            try {
                dao.insertCommunityNote(updatedNote)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating community note upvote locally: ${e.message}", e)
            }

            try {
                val updateData = mapOf(
                    "upvotedBy" to currentList,
                    "upvotes" to currentList.size
                )
                firestore.collection("community_notes").document(noteId).set(updateData, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling upvote on note $noteId: ${e.message}", e)
            }
        }
    }

    // Backwards compatibility helpers
    fun uploadSharedNote(note: CommunityNote) = uploadCommunityNote(note)
    fun upvoteSharedNote(noteId: String, userId: String) = toggleUpvoteCommunityNote(noteId, userId)

    // Forum Operations
    fun getPostsByTag(tag: String?): Flow<List<ForumPost>> {
        return forumPosts.map { list ->
            when (tag) {
                null, "All" -> list
                "Hot" -> list.sortedByDescending { it.upvotes }
                "Rants" -> list.filter { it.tag.equals("Rant", ignoreCase = true) || it.tag.equals("Rants", ignoreCase = true) }
                "Memes" -> list.filter { it.tag.equals("Meme", ignoreCase = true) || it.tag.equals("Memes", ignoreCase = true) }
                "Academics" -> list.filter { it.tag.equals("Academics", ignoreCase = true) }
                "Campus Life" -> list.filter { it.tag.equals("Campus Life", ignoreCase = true) }
                "Confession" -> list.filter { it.tag.equals("Confession", ignoreCase = true) || it.tag.equals("Confessions", ignoreCase = true) }
                else -> list.filter { it.tag.equals(tag, ignoreCase = true) }
            }
        }
    }

    fun getCommentsForPost(postId: String): Flow<List<ForumComment>> {
        return _forumComments.map { map ->
            map[postId] ?: emptyList()
        }
    }

    fun createPost(post: ForumPost) {
        val postId = if (post.id.isNotBlank()) post.id else UUID.randomUUID().toString()
        val completePost = post.copy(id = postId)

        val current = _forumPosts.value.toMutableList()
        if (current.none { it.id == postId }) {
            current.add(0, completePost)
            _forumPosts.value = current
        }

        scope.launch {
            // STEP 1: Explicit Local Room SQLite Insert
            try {
                dao.insertForumPost(completePost)
                Log.d(TAG, "Successfully inserted forum post ${completePost.id} into local Room database")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting forum post ${completePost.id} into local Room database: ${e.message}", e)
            }

            // STEP 2: Firebase Cloud Sync
            try {
                val map = hashMapOf<String, Any>(
                    "id" to completePost.id,
                    "authorUsername" to completePost.authorUsername,
                    "author" to completePost.authorUsername,
                    "authorDept" to completePost.authorDept,
                    "department" to completePost.authorDept,
                    "content" to completePost.content,
                    "tag" to completePost.tag,
                    "timestamp" to if (completePost.timestamp != 0L) completePost.timestamp else System.currentTimeMillis(),
                    "upvotedBy" to completePost.upvotedBy,
                    "upvotes" to completePost.upvotes,
                    "commentCount" to completePost.commentCount
                )
                if (!completePost.imageUrl.isNullOrBlank()) {
                    map["imageUrl"] = completePost.imageUrl!!
                    map["downloadUrl"] = completePost.imageUrl!!
                }

                // Must use .collection("forum_posts").document(post.id).set(map) to sync Room and Firestore
                firestore.collection("forum_posts").document(completePost.id).set(map, SetOptions.merge()).await()
                Log.d(TAG, "Successfully synced forum post ${completePost.id} to Firestore")
            } catch (e: Exception) {
                Log.e(TAG, "Error writing forum post ${completePost.id} to Firestore: ${e.message}", e)
            }
        }
    }

    fun getDeviceId(): String {
        return try {
            val androidId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
                androidId
            } else {
                getOrCreatePersistentUuid()
            }
        } catch (e: Exception) {
            getOrCreatePersistentUuid()
        }
    }

    private fun getOrCreatePersistentUuid(): String {
        val prefs = context.getSharedPreferences("app_device_identity", Context.MODE_PRIVATE)
        var savedId = prefs.getString("device_user_id", null)
        if (savedId.isNullOrBlank()) {
            savedId = UUID.randomUUID().toString()
            prefs.edit().putString("device_user_id", savedId).apply()
        }
        return savedId
    }

    // FIX 3: User Profile Cloud Sync - silently push UserProfile data to collection "users"
    fun syncUserProfileSilently(profile: UserProfile) {
        val docId = getDeviceId()
        scope.launch {
            try {
                ensureAuthenticated()
                val userData = hashMapOf<String, Any>(
                    "name" to (profile.name ?: ""),
                    "username" to profile.username,
                    "authorUsername" to profile.username,
                    "department" to profile.department,
                    "semester" to profile.semester,
                    "deviceId" to docId,
                    "notificationsEnabled" to profile.notificationsEnabled,
                    "themeMode" to profile.themeMode,
                    "updatedAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(docId).set(userData, SetOptions.merge()).await()
                Log.d(TAG, "Silently pushed UserProfile to users/$docId")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing user profile: ${e.message}", e)
            }
        }
    }

    fun toggleUpvotePost(postId: String, userId: String) {
        val post = _forumPosts.value.find { it.id == postId } ?: return
        val currentList = post.upvotedBy.toMutableList()
        val isUpvoted = currentList.contains(userId)
        if (isUpvoted) {
            currentList.remove(userId)
        } else {
            currentList.add(userId)
        }
        val updatedPost = post.copy(upvotedBy = currentList)
        _forumPosts.value = _forumPosts.value.map { if (it.id == postId) updatedPost else it }

        scope.launch {
            try {
                dao.insertForumPost(updatedPost)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating post upvote locally: ${e.message}", e)
            }

            try {
                val updateData = mapOf(
                    "upvotedBy" to currentList,
                    "upvotes" to currentList.size
                )
                firestore.collection("forum_posts").document(postId).set(updateData, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling upvote on post $postId: ${e.message}", e)
            }
        }
    }

    fun addComment(comment: ForumComment) {
        val commentId = if (comment.id.isNotBlank()) comment.id else UUID.randomUUID().toString()
        val completeComment = comment.copy(id = commentId)

        val map = _forumComments.value.toMutableMap()
        val list = map[comment.postId]?.toMutableList() ?: mutableListOf()
        list.add(completeComment)
        map[comment.postId] = list
        _forumComments.value = map

        val updatedPosts = _forumPosts.value.map {
            if (it.id == comment.postId) it.copy(commentCount = it.commentCount + 1) else it
        }
        _forumPosts.value = updatedPosts

        scope.launch {
            try {
                val cMap = hashMapOf(
                    "id" to completeComment.id,
                    "postId" to completeComment.postId,
                    "authorUsername" to completeComment.authorUsername,
                    "content" to completeComment.content,
                    "timestamp" to completeComment.timestamp
                )
                firestore.collection("forum_comments").document(commentId).set(cMap, SetOptions.merge()).await()
                firestore.collection("forum_posts").document(comment.postId).set(
                    mapOf("commentCount" to FieldValue.increment(1)),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Log.e(TAG, "Error writing comment to Firestore: ${e.message}", e)
            }
        }
    }
}
