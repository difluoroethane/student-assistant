package com.example.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _appDao: Lazy<AppDao> = lazy {
    AppDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(6,
        "c07acd77522549003e7461f30ceea83d", "f72ac4932060300197d683f2509320ba") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `announcements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `pinned` INTEGER NOT NULL, `imageUrl` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `subjects` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `code` TEXT, `department` TEXT NOT NULL, `semester` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `modules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subjectId` INTEGER NOT NULL, `title` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `progressStatus` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `moduleId` INTEGER NOT NULL, `title` TEXT NOT NULL, `fileType` TEXT NOT NULL, `content` TEXT NOT NULL, `uploadedDate` INTEGER NOT NULL, `isBookmarked` INTEGER NOT NULL, `authorUsername` TEXT NOT NULL, `department` TEXT NOT NULL, `subject` TEXT NOT NULL, `downloadUrl` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `assignments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subjectId` INTEGER NOT NULL, `title` TEXT NOT NULL, `dueDate` INTEGER NOT NULL, `priority` TEXT NOT NULL, `status` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `exams` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subjectId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `modulesCovered` TEXT NOT NULL, `notes` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `timetable_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `day` TEXT NOT NULL, `startTime` TEXT NOT NULL, `endTime` TEXT NOT NULL, `subjectId` INTEGER NOT NULL, `room` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER NOT NULL, `name` TEXT, `username` TEXT NOT NULL, `department` TEXT NOT NULL, `semester` TEXT NOT NULL, `notificationsEnabled` INTEGER NOT NULL, `themeMode` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cloud_announcements` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `pinned` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `community_notes` (`id` TEXT NOT NULL, `subject` TEXT NOT NULL, `moduleTitle` TEXT NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, `downloadUrl` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `upvotedBy` TEXT NOT NULL, `authorUsername` TEXT NOT NULL, `authorDept` TEXT NOT NULL, `department` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `forum_posts` (`id` TEXT NOT NULL, `authorUsername` TEXT NOT NULL, `authorDept` TEXT NOT NULL, `department` TEXT NOT NULL, `content` TEXT NOT NULL, `tag` TEXT NOT NULL, `upvotedBy` TEXT NOT NULL, `commentCount` INTEGER NOT NULL, `imageUrl` TEXT, `downloadUrl` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c07acd77522549003e7461f30ceea83d')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `announcements`")
        connection.execSQL("DROP TABLE IF EXISTS `subjects`")
        connection.execSQL("DROP TABLE IF EXISTS `modules`")
        connection.execSQL("DROP TABLE IF EXISTS `notes`")
        connection.execSQL("DROP TABLE IF EXISTS `assignments`")
        connection.execSQL("DROP TABLE IF EXISTS `exams`")
        connection.execSQL("DROP TABLE IF EXISTS `timetable_entries`")
        connection.execSQL("DROP TABLE IF EXISTS `user_profile`")
        connection.execSQL("DROP TABLE IF EXISTS `cloud_announcements`")
        connection.execSQL("DROP TABLE IF EXISTS `community_notes`")
        connection.execSQL("DROP TABLE IF EXISTS `forum_posts`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsAnnouncements: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAnnouncements.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnnouncements.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnnouncements.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnnouncements.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnnouncements.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnnouncements.put("pinned", TableInfo.Column("pinned", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnnouncements.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAnnouncements: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAnnouncements: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAnnouncements: TableInfo = TableInfo("announcements", _columnsAnnouncements,
            _foreignKeysAnnouncements, _indicesAnnouncements)
        val _existingAnnouncements: TableInfo = read(connection, "announcements")
        if (!_infoAnnouncements.equals(_existingAnnouncements)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |announcements(com.example.data.Announcement).
              | Expected:
              |""".trimMargin() + _infoAnnouncements + """
              |
              | Found:
              |""".trimMargin() + _existingAnnouncements)
        }
        val _columnsSubjects: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSubjects.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSubjects.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSubjects.put("code", TableInfo.Column("code", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSubjects.put("department", TableInfo.Column("department", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSubjects.put("semester", TableInfo.Column("semester", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSubjects: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSubjects: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSubjects: TableInfo = TableInfo("subjects", _columnsSubjects, _foreignKeysSubjects,
            _indicesSubjects)
        val _existingSubjects: TableInfo = read(connection, "subjects")
        if (!_infoSubjects.equals(_existingSubjects)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |subjects(com.example.data.Subject).
              | Expected:
              |""".trimMargin() + _infoSubjects + """
              |
              | Found:
              |""".trimMargin() + _existingSubjects)
        }
        val _columnsModules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsModules.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModules.put("subjectId", TableInfo.Column("subjectId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModules.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModules.put("orderIndex", TableInfo.Column("orderIndex", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModules.put("progressStatus", TableInfo.Column("progressStatus", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysModules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesModules: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoModules: TableInfo = TableInfo("modules", _columnsModules, _foreignKeysModules,
            _indicesModules)
        val _existingModules: TableInfo = read(connection, "modules")
        if (!_infoModules.equals(_existingModules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |modules(com.example.data.Module).
              | Expected:
              |""".trimMargin() + _infoModules + """
              |
              | Found:
              |""".trimMargin() + _existingModules)
        }
        val _columnsNotes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotes.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("moduleId", TableInfo.Column("moduleId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("fileType", TableInfo.Column("fileType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("uploadedDate", TableInfo.Column("uploadedDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("isBookmarked", TableInfo.Column("isBookmarked", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("authorUsername", TableInfo.Column("authorUsername", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("department", TableInfo.Column("department", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("subject", TableInfo.Column("subject", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("downloadUrl", TableInfo.Column("downloadUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotes.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNotes: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoNotes: TableInfo = TableInfo("notes", _columnsNotes, _foreignKeysNotes,
            _indicesNotes)
        val _existingNotes: TableInfo = read(connection, "notes")
        if (!_infoNotes.equals(_existingNotes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notes(com.example.data.Note).
              | Expected:
              |""".trimMargin() + _infoNotes + """
              |
              | Found:
              |""".trimMargin() + _existingNotes)
        }
        val _columnsAssignments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAssignments.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssignments.put("subjectId", TableInfo.Column("subjectId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssignments.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssignments.put("dueDate", TableInfo.Column("dueDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssignments.put("priority", TableInfo.Column("priority", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssignments.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAssignments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAssignments: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAssignments: TableInfo = TableInfo("assignments", _columnsAssignments,
            _foreignKeysAssignments, _indicesAssignments)
        val _existingAssignments: TableInfo = read(connection, "assignments")
        if (!_infoAssignments.equals(_existingAssignments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |assignments(com.example.data.Assignment).
              | Expected:
              |""".trimMargin() + _infoAssignments + """
              |
              | Found:
              |""".trimMargin() + _existingAssignments)
        }
        val _columnsExams: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExams.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExams.put("subjectId", TableInfo.Column("subjectId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExams.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExams.put("modulesCovered", TableInfo.Column("modulesCovered", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExams.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExams: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExams: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExams: TableInfo = TableInfo("exams", _columnsExams, _foreignKeysExams,
            _indicesExams)
        val _existingExams: TableInfo = read(connection, "exams")
        if (!_infoExams.equals(_existingExams)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |exams(com.example.data.Exam).
              | Expected:
              |""".trimMargin() + _infoExams + """
              |
              | Found:
              |""".trimMargin() + _existingExams)
        }
        val _columnsTimetableEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTimetableEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTimetableEntries.put("day", TableInfo.Column("day", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTimetableEntries.put("startTime", TableInfo.Column("startTime", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTimetableEntries.put("endTime", TableInfo.Column("endTime", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTimetableEntries.put("subjectId", TableInfo.Column("subjectId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTimetableEntries.put("room", TableInfo.Column("room", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTimetableEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTimetableEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTimetableEntries: TableInfo = TableInfo("timetable_entries",
            _columnsTimetableEntries, _foreignKeysTimetableEntries, _indicesTimetableEntries)
        val _existingTimetableEntries: TableInfo = read(connection, "timetable_entries")
        if (!_infoTimetableEntries.equals(_existingTimetableEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |timetable_entries(com.example.data.TimetableEntry).
              | Expected:
              |""".trimMargin() + _infoTimetableEntries + """
              |
              | Found:
              |""".trimMargin() + _existingTimetableEntries)
        }
        val _columnsUserProfile: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUserProfile.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("name", TableInfo.Column("name", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("username", TableInfo.Column("username", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("department", TableInfo.Column("department", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("semester", TableInfo.Column("semester", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("notificationsEnabled", TableInfo.Column("notificationsEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("themeMode", TableInfo.Column("themeMode", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserProfile: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUserProfile: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUserProfile: TableInfo = TableInfo("user_profile", _columnsUserProfile,
            _foreignKeysUserProfile, _indicesUserProfile)
        val _existingUserProfile: TableInfo = read(connection, "user_profile")
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |user_profile(com.example.data.UserProfile).
              | Expected:
              |""".trimMargin() + _infoUserProfile + """
              |
              | Found:
              |""".trimMargin() + _existingUserProfile)
        }
        val _columnsCloudAnnouncements: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCloudAnnouncements.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCloudAnnouncements.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCloudAnnouncements.put("description", TableInfo.Column("description", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCloudAnnouncements.put("category", TableInfo.Column("category", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCloudAnnouncements.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCloudAnnouncements.put("pinned", TableInfo.Column("pinned", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCloudAnnouncements: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCloudAnnouncements: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCloudAnnouncements: TableInfo = TableInfo("cloud_announcements",
            _columnsCloudAnnouncements, _foreignKeysCloudAnnouncements, _indicesCloudAnnouncements)
        val _existingCloudAnnouncements: TableInfo = read(connection, "cloud_announcements")
        if (!_infoCloudAnnouncements.equals(_existingCloudAnnouncements)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cloud_announcements(com.example.data.CloudAnnouncement).
              | Expected:
              |""".trimMargin() + _infoCloudAnnouncements + """
              |
              | Found:
              |""".trimMargin() + _existingCloudAnnouncements)
        }
        val _columnsCommunityNotes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCommunityNotes.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("subject", TableInfo.Column("subject", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("moduleTitle", TableInfo.Column("moduleTitle", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("author", TableInfo.Column("author", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("downloadUrl", TableInfo.Column("downloadUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("upvotedBy", TableInfo.Column("upvotedBy", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("authorUsername", TableInfo.Column("authorUsername", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("authorDept", TableInfo.Column("authorDept", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityNotes.put("department", TableInfo.Column("department", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCommunityNotes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCommunityNotes: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCommunityNotes: TableInfo = TableInfo("community_notes", _columnsCommunityNotes,
            _foreignKeysCommunityNotes, _indicesCommunityNotes)
        val _existingCommunityNotes: TableInfo = read(connection, "community_notes")
        if (!_infoCommunityNotes.equals(_existingCommunityNotes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |community_notes(com.example.data.CommunityNote).
              | Expected:
              |""".trimMargin() + _infoCommunityNotes + """
              |
              | Found:
              |""".trimMargin() + _existingCommunityNotes)
        }
        val _columnsForumPosts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsForumPosts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("authorUsername", TableInfo.Column("authorUsername", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("authorDept", TableInfo.Column("authorDept", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("department", TableInfo.Column("department", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("tag", TableInfo.Column("tag", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("upvotedBy", TableInfo.Column("upvotedBy", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("commentCount", TableInfo.Column("commentCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("downloadUrl", TableInfo.Column("downloadUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsForumPosts.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysForumPosts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesForumPosts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoForumPosts: TableInfo = TableInfo("forum_posts", _columnsForumPosts,
            _foreignKeysForumPosts, _indicesForumPosts)
        val _existingForumPosts: TableInfo = read(connection, "forum_posts")
        if (!_infoForumPosts.equals(_existingForumPosts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |forum_posts(com.example.data.ForumPost).
              | Expected:
              |""".trimMargin() + _infoForumPosts + """
              |
              | Found:
              |""".trimMargin() + _existingForumPosts)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "announcements", "subjects",
        "modules", "notes", "assignments", "exams", "timetable_entries", "user_profile",
        "cloud_announcements", "community_notes", "forum_posts")
  }

  public override fun clearAllTables() {
    super.performClear(false, "announcements", "subjects", "modules", "notes", "assignments",
        "exams", "timetable_entries", "user_profile", "cloud_announcements", "community_notes",
        "forum_posts")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(AppDao::class, AppDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun appDao(): AppDao = _appDao.value
}
