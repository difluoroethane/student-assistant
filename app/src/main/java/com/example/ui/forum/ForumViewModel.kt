package com.example.ui.forum

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.ForumComment
import com.example.data.ForumPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ForumViewModel(
    private val repository: AppRepository,
    private val dao: com.example.data.AppDao = repository.dao
) : ViewModel() {

    private val _selectedTag = MutableStateFlow("All")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()

    val posts: StateFlow<List<ForumPost>> = _selectedTag.flatMapLatest { tag ->
        repository.getForumPosts(tag)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activePost = MutableStateFlow<ForumPost?>(null)
    val activePost: StateFlow<ForumPost?> = _activePost.asStateFlow()

    val activeComments: StateFlow<List<ForumComment>> = _activePost.flatMapLatest { post ->
        if (post != null) {
            repository.getForumComments(post.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submitError = MutableStateFlow<String?>(null)
    val submitError: StateFlow<String?> = _submitError.asStateFlow()

    init {
        viewModelScope.launch {
            repository.syncWithCloud()
        }
    }

    fun selectTag(tag: String) {
        _selectedTag.value = tag
    }

    fun upvotePost(postId: String, userId: String) {
        viewModelScope.launch {
            repository.upvoteForumPost(postId, userId)
        }
    }

    fun createPost(
        content: String,
        tag: String,
        authorUsername: String,
        authorDept: String,
        imageUri: Uri? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (content.isBlank() && imageUri == null) {
            val err = "Post cannot be empty"
            _submitError.value = err
            onError(err)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSubmitting.value = true
            _submitError.value = null
            try {
                val postId = UUID.randomUUID().toString()

                // STEP 1: Save the Post to the LOCAL Room Database immediately (dao.insert...)
                // This guarantees it appears in the local UI immediately.
                val initialPost = ForumPost(
                    id = postId,
                    authorUsername = authorUsername,
                    authorDept = authorDept,
                    department = authorDept,
                    content = content.trim(),
                    tag = tag,
                    upvotedBy = listOf(authorUsername),
                    commentCount = 0,
                    imageUrl = null,
                    downloadUrl = "",
                    timestamp = System.currentTimeMillis()
                )
                dao.insertForumPost(initialPost)

                // Dismiss dialog and notify UI immediately after local save
                withContext(Dispatchers.Main) {
                    _isSubmitting.value = false
                    onSuccess()
                }

                // STEP 2: If a file/image Uri is attached, call uploadFileToStorage(uri). Wait for the downloadUrl.
                var finalPost = initialPost
                if (imageUri != null) {
                    try {
                        val downloadUrl = repository.uploadForumImage(imageUri)
                        if (downloadUrl.isNotBlank()) {
                            // STEP 3: Update the local Room database with the new downloadUrl.
                            finalPost = initialPost.copy(
                                imageUrl = downloadUrl,
                                downloadUrl = downloadUrl
                            )
                            dao.insertForumPost(finalPost)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ForumViewModel", "Failed to upload forum image: ${e.message}", e)
                    }
                }

                // STEP 4: Call firestoreService.createPost() to push the final object to Cloud Firestore.
                repository.createForumPost(finalPost)
            } catch (e: Exception) {
                android.util.Log.e("ForumViewModel", "Error in createPost pipeline: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _isSubmitting.value = false
                    onSuccess()
                }
            }
        }
    }

    fun openPostThread(post: ForumPost) {
        _activePost.value = post
    }

    fun closePostThread() {
        _activePost.value = null
    }

    fun addComment(content: String, authorUsername: String) {
        val currentPost = _activePost.value ?: return
        if (content.isBlank()) return
        val comment = ForumComment(
            postId = currentPost.id,
            authorUsername = authorUsername,
            content = content.trim(),
            timestamp = System.currentTimeMillis()
        )
        repository.addForumComment(comment)
        _activePost.value = currentPost.copy(commentCount = currentPost.commentCount + 1)
    }
}
