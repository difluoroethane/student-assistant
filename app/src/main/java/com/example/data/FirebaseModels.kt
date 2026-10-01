package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forum_posts")
data class ForumPost(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val authorUsername: String = "@AnonCowboy",
    val authorDept: String = "Engineering",
    val department: String = "Engineering",
    val content: String = "",
    val tag: String = "Campus Life", // "Rant", "Campus Life", "Academics", "Confession", "Meme"
    val upvotedBy: List<String> = emptyList(),
    val commentCount: Int = 0,
    val imageUrl: String? = null,
    val downloadUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val upvotes: Int get() = upvotedBy.size
}

data class ForumComment(
    val id: String = "",
    val postId: String = "",
    val authorUsername: String = "@AnonCowboy",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Legacy alias / projection for community note UI screens
typealias SharedNote = CommunityNote
