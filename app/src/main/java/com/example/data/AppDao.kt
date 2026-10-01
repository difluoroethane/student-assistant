package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Announcements
    @Query("SELECT * FROM announcements ORDER BY pinned DESC, timestamp DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    // Subjects
    @Query("SELECT * FROM subjects WHERE department = :department AND semester = :semester")
    fun getSubjects(department: String, semester: String): Flow<List<Subject>>
    
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectById(id: Int): Flow<Subject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    // Modules
    @Query("SELECT * FROM modules WHERE subjectId = :subjectId ORDER BY orderIndex ASC")
    fun getModulesForSubject(subjectId: Int): Flow<List<Module>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: Module): Long

    @Update
    suspend fun updateModule(module: Module)

    // Notes
    @Query("SELECT * FROM notes WHERE moduleId = :moduleId ORDER BY uploadedDate DESC")
    fun getNotesForModule(moduleId: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isBookmarked = 1")
    fun getBookmarkedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<Note?>

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    // Assignments
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
    fun getAllAssignments(): Flow<List<Assignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment)

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    // Exams
    @Query("SELECT * FROM exams ORDER BY date ASC")
    fun getAllExams(): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam)

    // Timetable
    @Query("SELECT * FROM timetable_entries ORDER BY startTime ASC")
    fun getAllTimetableEntries(): Flow<List<TimetableEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableEntry(entry: TimetableEntry)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Cloud Announcements Cache (Experiment 4)
    @Query("SELECT * FROM cloud_announcements ORDER BY pinned DESC, timestamp DESC")
    fun getAllCloudAnnouncements(): Flow<List<CloudAnnouncement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloudAnnouncements(announcements: List<CloudAnnouncement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloudAnnouncement(announcement: CloudAnnouncement)

    // Community Notes Cache (Experiment 4)
    @Query("SELECT * FROM community_notes ORDER BY timestamp DESC")
    fun getAllCommunityNotes(): Flow<List<CommunityNote>>

    @Query("SELECT * FROM community_notes WHERE id = :id LIMIT 1")
    suspend fun getCommunityNoteById(id: String): CommunityNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityNotes(notes: List<CommunityNote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityNote(note: CommunityNote)

    // Forum Posts
    @Query("SELECT * FROM forum_posts ORDER BY timestamp DESC")
    fun getAllForumPosts(): Flow<List<ForumPost>>

    @Query("SELECT * FROM forum_posts WHERE id = :id LIMIT 1")
    suspend fun getForumPostById(id: String): ForumPost?

    @Query("SELECT * FROM forum_posts WHERE tag = :tag ORDER BY timestamp DESC")
    fun getForumPostsByTag(tag: String): Flow<List<ForumPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumPost(post: ForumPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumPosts(posts: List<ForumPost>)
}
