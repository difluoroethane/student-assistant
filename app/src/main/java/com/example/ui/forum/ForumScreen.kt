package com.example.ui.forum

import android.net.Uri
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.ForumComment
import com.example.data.ForumPost
import com.example.data.UserProfile
import com.example.ui.components.BrutalBadge
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalGreen
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.BrutalYellow
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.CatConfession
import com.example.ui.theme.CatMemes
import com.example.ui.theme.LocalBrutalPalette

@Composable
fun ForumScreen(
    viewModel: ForumViewModel,
    userProfile: UserProfile,
    onUpdateUsername: (String) -> Unit
) {
    val palette = LocalBrutalPalette.current
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val activePost by viewModel.activePost.collectAsStateWithLifecycle()
    val comments by viewModel.activeComments.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val submitError by viewModel.submitError.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditHandleDialog by remember { mutableStateOf(false) }

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
                    onClick = { showCreateDialog = true },
                    shape = RectangleShape,
                    containerColor = BrutalOrange,
                    contentColor = BrutalWhite,
                    modifier = Modifier
                        .border(3.dp, palette.border, RectangleShape)
                        .testTag("create_post_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Post")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "NEW POST",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header: The Quad & User Handle Badge
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(2.dp, palette.border, RectangleShape)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "THE QUAD",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                            color = palette.textPrimary
                        )
                        Text(
                            "ANONYMOUS CAMPUS FEED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            ),
                            color = palette.accentOrange
                        )
                    }

                    // User Identity Handle Badge
                    Row(
                        modifier = Modifier
                            .border(2.dp, palette.border, RectangleShape)
                            .background(palette.background, RectangleShape)
                            .clickable { showEditHandleDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("user_handle_badge"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "POSTING AS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = palette.textSecondary
                            )
                            Text(
                                text = userProfile.username,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BrutalOrange
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Handle",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tag Filter Row
                val tags = listOf("All", "Hot", "Campus Life", "Rants", "Memes", "Academics", "Confession")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tags) { tag ->
                        val isSelected = tag == selectedTag
                        val chipBg = if (isSelected) BrutalOrange else palette.surface
                        val chipText = if (isSelected) BrutalWhite else palette.textPrimary

                        Box(
                            modifier = Modifier
                                .border(2.dp, palette.border, RectangleShape)
                                .background(chipBg, RectangleShape)
                                .clickable { viewModel.selectTag(tag) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("tag_filter_$tag")
                        ) {
                            Text(
                                text = tag.uppercase(),
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

            // Posts Feed
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (posts.isEmpty()) {
                    item {
                        BrutalCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderWidth = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "NO POSTS FOUND",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Be the first to post something in this category!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textSecondary
                                )
                            }
                        }
                    }
                }

                items(posts, key = { it.id }) { post ->
                    ForumPostCard(
                        post = post,
                        currentUsername = userProfile.username,
                        onUpvote = { viewModel.upvotePost(post.id, userProfile.username) },
                        onOpenThread = { viewModel.openPostThread(post) }
                    )
                }
            }
        }
    }

    // Create Post Dialog
    if (showCreateDialog) {
        CreatePostDialog(
            userProfile = userProfile,
            isSubmitting = isSubmitting,
            submitError = submitError,
            onDismiss = { showCreateDialog = false },
            onSubmit = { content, tag, imageUri ->
                viewModel.createPost(
                    content = content,
                    tag = tag,
                    authorUsername = userProfile.username,
                    authorDept = userProfile.department,
                    imageUri = imageUri,
                    onSuccess = {
                        showCreateDialog = false
                    },
                    onError = {}
                )
            }
        )
    }

    // Edit Handle Dialog
    if (showEditHandleDialog) {
        EditHandleDialog(
            currentHandle = userProfile.username,
            onDismiss = { showEditHandleDialog = false },
            onSave = { newHandle ->
                onUpdateUsername(newHandle)
                showEditHandleDialog = false
            }
        )
    }

    // Thread Discussion Dialog
    if (activePost != null) {
        PostThreadDialog(
            post = activePost!!,
            comments = comments,
            userProfile = userProfile,
            onDismiss = { viewModel.closePostThread() },
            onUpvote = { viewModel.upvotePost(activePost!!.id, userProfile.username) },
            onAddComment = { commentContent ->
                viewModel.addComment(commentContent, userProfile.username)
            }
        )
    }
}

@Composable
fun ForumPostCard(
    post: ForumPost,
    currentUsername: String = "@AnonCowboy",
    onUpvote: () -> Unit,
    onOpenThread: () -> Unit
) {
    val palette = LocalBrutalPalette.current

    val tagColor = when (post.tag) {
        "Rant", "Rants" -> BrutalOrange
        "Campus Life" -> BrutalGreen
        "Academics" -> CatAcademic
        "Meme", "Memes" -> CatMemes
        "Confession" -> CatConfession
        else -> BrutalYellow
    }

    val isUpvoted = post.upvotedBy.contains(currentUsername)

    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpenThread,
        borderWidth = 3.dp,
        shadowOffset = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Tag, Author, Department, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrutalTag(
                    text = post.tag,
                    backgroundColor = tagColor,
                    textColor = BrutalWhite
                )

                Text(
                    text = DateUtils.getRelativeTimeSpanString(post.timestamp).toString().uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = palette.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Author metadata
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.authorUsername,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = BrutalOrange
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${post.authorDept}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = palette.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content text
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        lineHeight = 22.sp
                    ),
                    color = palette.textPrimary
                )
            }

            // Attached Image if available
            if (!post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post image attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .border(3.dp, palette.border, RectangleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upvote Button (checks if already upvoted)
                Row(
                    modifier = Modifier
                        .border(2.dp, palette.border, RectangleShape)
                        .background(if (isUpvoted) BrutalNeonYellow else palette.surface, RectangleShape)
                        .clickable { onUpvote() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("upvote_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Upvote",
                        tint = if (isUpvoted) BrutalBlack else BrutalOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.upvotes}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = if (isUpvoted) BrutalBlack else palette.textPrimary
                    )
                }

                // Comment Button
                Row(
                    modifier = Modifier
                        .border(2.dp, palette.border, RectangleShape)
                        .background(palette.surface, RectangleShape)
                        .clickable { onOpenThread() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("comments_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentCount} REPLIES",
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
fun CreatePostDialog(
    userProfile: UserProfile,
    isSubmitting: Boolean = false,
    submitError: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (content: String, tag: String, imageUri: Uri?) -> Unit
) {
    val palette = LocalBrutalPalette.current
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Campus Life") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }
    val tags = listOf("Campus Life", "Rant", "Academics", "Confession", "Meme")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            localError = null
        }
    }

    BasicAlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() }
    ) {
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
                    Text(
                        "CREATE POST",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    if (!isSubmitting) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "CHOOSE TAG:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = palette.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(tags) { tag ->
                        val isSelected = tag == selectedTag
                        Box(
                            modifier = Modifier
                                .border(2.dp, palette.border, RectangleShape)
                                .background(if (isSelected) BrutalOrange else palette.surface, RectangleShape)
                                .clickable { selectedTag = tag }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag.uppercase(),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) BrutalWhite else palette.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        localError = null
                    },
                    placeholder = { Text("What's on your mind on campus?", color = palette.textSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("post_content_input"),
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

                // Image Picker / Preview Section
                if (selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, palette.border, RectangleShape)
                            .background(palette.background)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected image preview",
                                modifier = Modifier
                                    .size(60.dp)
                                    .border(2.dp, palette.border, RectangleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "IMAGE ATTACHED",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = palette.accentOrange
                                )
                                Text(
                                    text = "Will be uploaded to Storage",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = palette.textSecondary
                                )
                            }
                            IconButton(onClick = { selectedImageUri = null }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Image", tint = Color(0xFFD32F2F))
                            }
                        }
                    }
                } else {
                    BrutalButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        backgroundColor = palette.surface,
                        contentColor = palette.textPrimary,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pick_forum_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach Image",
                            modifier = Modifier.size(18.dp),
                            tint = BrutalOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "ATTACH IMAGE (OPTIONAL)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }

                if (localError != null || submitError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, palette.border, RectangleShape)
                            .background(Color(0xFFFFEBEE))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = localError ?: submitError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFC62828)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BrutalButton(
                    onClick = {
                        if (content.isBlank() && selectedImageUri == null) {
                            localError = "Please enter some text or select an image."
                            return@BrutalButton
                        }
                        onSubmit(content, selectedTag, selectedImageUri)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_post_button"),
                    backgroundColor = if (isSubmitting) BrutalNeonYellow else BrutalOrange,
                    contentColor = if (isSubmitting) BrutalBlack else BrutalWhite
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = BrutalBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "POSTING...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                    } else {
                        Text(
                            "POST TO THE QUAD",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHandleDialog(
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    "EDIT HANDLE",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = palette.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Handle (e.g. BitFlipper)") },
                    prefix = { Text("@", fontWeight = FontWeight.Bold, color = palette.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
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
fun PostThreadDialog(
    post: ForumPost,
    comments: List<ForumComment>,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onUpvote: () -> Unit,
    onAddComment: (String) -> Unit
) {
    val palette = LocalBrutalPalette.current
    var newCommentText by remember { mutableStateOf("") }
    val isUpvoted = post.upvotedBy.contains(userProfile.username)

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .border(4.dp, palette.border, RectangleShape),
            shape = RectangleShape,
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "THREAD DISCUSSION",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable content area: Original post + comments
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Original post snippet
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, palette.border, RectangleShape)
                            .background(palette.background, RectangleShape)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    post.authorUsername,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = BrutalOrange
                                    )
                                )
                                Text(
                                    DateUtils.getRelativeTimeSpanString(post.timestamp).toString().uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = palette.textSecondary
                                )
                            }
                            if (post.content.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    post.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textPrimary
                                )
                            }

                            if (!post.imageUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                AsyncImage(
                                    model = post.imageUrl,
                                    contentDescription = "Post image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 220.dp)
                                        .border(2.dp, palette.border, RectangleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Upvote in thread
                            Row(
                                modifier = Modifier
                                    .border(2.dp, palette.border, RectangleShape)
                                    .background(if (isUpvoted) BrutalNeonYellow else palette.surface, RectangleShape)
                                    .clickable { onUpvote() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Upvote",
                                    tint = if (isUpvoted) BrutalBlack else BrutalOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${post.upvotes} UPVOTES",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = if (isUpvoted) BrutalBlack else palette.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "REPLIES (${comments.size})",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = palette.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (comments.isEmpty()) {
                        Text(
                            "No replies yet. Start the conversation!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    comments.forEach { comment ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, palette.border, RectangleShape)
                                .background(palette.surface, RectangleShape)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        comment.authorUsername,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = BrutalOrange
                                    )
                                    Text(
                                        DateUtils.getRelativeTimeSpanString(comment.timestamp).toString().uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = palette.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    comment.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Reply Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Write a reply...", color = palette.textSecondary, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reply_input"),
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
                    Spacer(modifier = Modifier.width(8.dp))
                    BrutalButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment(newCommentText)
                                newCommentText = ""
                            }
                        },
                        backgroundColor = BrutalOrange,
                        contentColor = BrutalWhite,
                        modifier = Modifier.testTag("send_reply_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
