package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // Urgent, Event, Academic, General
    val timestamp: Long,
    val pinned: Boolean,
    val imageUrl: String? = null
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val code: String?,
    val department: String,
    val semester: String
)

@Entity(tableName = "modules")
data class Module(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val title: String,
    val orderIndex: Int,
    val progressStatus: String // "Not Started", "In Progress", "Studied"
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moduleId: Int = 0,
    val title: String = "",
    val fileType: String = "PDF",
    val content: String = "",
    val uploadedDate: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false,
    val authorUsername: String = "@AnonCowboy",
    val department: String = "Engineering",
    val subject: String = "",
    val downloadUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val title: String,
    val dueDate: Long,
    val priority: String, // High, Medium, Low
    val status: String // Pending, Submitted
)

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val date: Long,
    val modulesCovered: String,
    val notes: String
)

@Entity(tableName = "timetable_entries")
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subjectId: Int,
    val room: String? = null
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String?,
    val username: String = "@AnonCowboy",
    val department: String,
    val semester: String,
    val notificationsEnabled: Boolean = true,
    val themeMode: String = "Light"
)

@Entity(tableName = "cloud_announcements")
data class CloudAnnouncement(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val timestamp: Long = 0L,
    val pinned: Boolean = false
)

@Entity(tableName = "community_notes")
data class CommunityNote(
    @PrimaryKey val id: String = "",
    val subject: String = "",
    val moduleTitle: String = "",
    val title: String = "",
    val author: String = "@AnonCowboy",
    val downloadUrl: String = "",
    val timestamp: Long = 0L,
    val upvotedBy: List<String> = emptyList(),
    val authorUsername: String = "@AnonCowboy",
    val authorDept: String = "Engineering",
    val department: String = "Engineering"
) {
    val upvotes: Int get() = upvotedBy.size
    val subjectName: String get() = subject
    val uploaderName: String get() = if (authorUsername.isNotBlank()) authorUsername else author
}
