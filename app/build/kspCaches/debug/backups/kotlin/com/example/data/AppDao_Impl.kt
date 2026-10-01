package com.example.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDao_Impl(
  __db: RoomDatabase,
) : AppDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAnnouncement: EntityInsertAdapter<Announcement>

  private val __insertAdapterOfSubject: EntityInsertAdapter<Subject>

  private val __insertAdapterOfModule: EntityInsertAdapter<Module>

  private val __insertAdapterOfNote: EntityInsertAdapter<Note>

  private val __insertAdapterOfAssignment: EntityInsertAdapter<Assignment>

  private val __insertAdapterOfExam: EntityInsertAdapter<Exam>

  private val __insertAdapterOfTimetableEntry: EntityInsertAdapter<TimetableEntry>

  private val __insertAdapterOfUserProfile: EntityInsertAdapter<UserProfile>

  private val __insertAdapterOfCloudAnnouncement: EntityInsertAdapter<CloudAnnouncement>

  private val __insertAdapterOfCommunityNote: EntityInsertAdapter<CommunityNote>

  private val __converters: Converters = Converters()

  private val __insertAdapterOfForumPost: EntityInsertAdapter<ForumPost>

  private val __updateAdapterOfModule: EntityDeleteOrUpdateAdapter<Module>

  private val __updateAdapterOfNote: EntityDeleteOrUpdateAdapter<Note>

  private val __updateAdapterOfAssignment: EntityDeleteOrUpdateAdapter<Assignment>
  init {
    this.__db = __db
    this.__insertAdapterOfAnnouncement = object : EntityInsertAdapter<Announcement>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `announcements` (`id`,`title`,`description`,`category`,`timestamp`,`pinned`,`imageUrl`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Announcement) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.category)
        statement.bindLong(5, entity.timestamp)
        val _tmp: Int = if (entity.pinned) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpImageUrl)
        }
      }
    }
    this.__insertAdapterOfSubject = object : EntityInsertAdapter<Subject>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `subjects` (`id`,`name`,`code`,`department`,`semester`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Subject) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.name)
        val _tmpCode: String? = entity.code
        if (_tmpCode == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpCode)
        }
        statement.bindText(4, entity.department)
        statement.bindText(5, entity.semester)
      }
    }
    this.__insertAdapterOfModule = object : EntityInsertAdapter<Module>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `modules` (`id`,`subjectId`,`title`,`orderIndex`,`progressStatus`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Module) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.subjectId.toLong())
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.orderIndex.toLong())
        statement.bindText(5, entity.progressStatus)
      }
    }
    this.__insertAdapterOfNote = object : EntityInsertAdapter<Note>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `notes` (`id`,`moduleId`,`title`,`fileType`,`content`,`uploadedDate`,`isBookmarked`,`authorUsername`,`department`,`subject`,`downloadUrl`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Note) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.moduleId.toLong())
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.fileType)
        statement.bindText(5, entity.content)
        statement.bindLong(6, entity.uploadedDate)
        val _tmp: Int = if (entity.isBookmarked) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindText(8, entity.authorUsername)
        statement.bindText(9, entity.department)
        statement.bindText(10, entity.subject)
        statement.bindText(11, entity.downloadUrl)
        statement.bindLong(12, entity.timestamp)
      }
    }
    this.__insertAdapterOfAssignment = object : EntityInsertAdapter<Assignment>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `assignments` (`id`,`subjectId`,`title`,`dueDate`,`priority`,`status`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Assignment) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.subjectId.toLong())
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.dueDate)
        statement.bindText(5, entity.priority)
        statement.bindText(6, entity.status)
      }
    }
    this.__insertAdapterOfExam = object : EntityInsertAdapter<Exam>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `exams` (`id`,`subjectId`,`date`,`modulesCovered`,`notes`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Exam) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.subjectId.toLong())
        statement.bindLong(3, entity.date)
        statement.bindText(4, entity.modulesCovered)
        statement.bindText(5, entity.notes)
      }
    }
    this.__insertAdapterOfTimetableEntry = object : EntityInsertAdapter<TimetableEntry>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `timetable_entries` (`id`,`day`,`startTime`,`endTime`,`subjectId`,`room`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TimetableEntry) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.day)
        statement.bindText(3, entity.startTime)
        statement.bindText(4, entity.endTime)
        statement.bindLong(5, entity.subjectId.toLong())
        val _tmpRoom: String? = entity.room
        if (_tmpRoom == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRoom)
        }
      }
    }
    this.__insertAdapterOfUserProfile = object : EntityInsertAdapter<UserProfile>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `user_profile` (`id`,`name`,`username`,`department`,`semester`,`notificationsEnabled`,`themeMode`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserProfile) {
        statement.bindLong(1, entity.id.toLong())
        val _tmpName: String? = entity.name
        if (_tmpName == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpName)
        }
        statement.bindText(3, entity.username)
        statement.bindText(4, entity.department)
        statement.bindText(5, entity.semester)
        val _tmp: Int = if (entity.notificationsEnabled) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.themeMode)
      }
    }
    this.__insertAdapterOfCloudAnnouncement = object : EntityInsertAdapter<CloudAnnouncement>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cloud_announcements` (`id`,`title`,`description`,`category`,`timestamp`,`pinned`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CloudAnnouncement) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.category)
        statement.bindLong(5, entity.timestamp)
        val _tmp: Int = if (entity.pinned) 1 else 0
        statement.bindLong(6, _tmp.toLong())
      }
    }
    this.__insertAdapterOfCommunityNote = object : EntityInsertAdapter<CommunityNote>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `community_notes` (`id`,`subject`,`moduleTitle`,`title`,`author`,`downloadUrl`,`timestamp`,`upvotedBy`,`authorUsername`,`authorDept`,`department`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CommunityNote) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.subject)
        statement.bindText(3, entity.moduleTitle)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.author)
        statement.bindText(6, entity.downloadUrl)
        statement.bindLong(7, entity.timestamp)
        val _tmp: String = __converters.fromStringList(entity.upvotedBy)
        statement.bindText(8, _tmp)
        statement.bindText(9, entity.authorUsername)
        statement.bindText(10, entity.authorDept)
        statement.bindText(11, entity.department)
      }
    }
    this.__insertAdapterOfForumPost = object : EntityInsertAdapter<ForumPost>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `forum_posts` (`id`,`authorUsername`,`authorDept`,`department`,`content`,`tag`,`upvotedBy`,`commentCount`,`imageUrl`,`downloadUrl`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ForumPost) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.authorUsername)
        statement.bindText(3, entity.authorDept)
        statement.bindText(4, entity.department)
        statement.bindText(5, entity.content)
        statement.bindText(6, entity.tag)
        val _tmp: String = __converters.fromStringList(entity.upvotedBy)
        statement.bindText(7, _tmp)
        statement.bindLong(8, entity.commentCount.toLong())
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpImageUrl)
        }
        statement.bindText(10, entity.downloadUrl)
        statement.bindLong(11, entity.timestamp)
      }
    }
    this.__updateAdapterOfModule = object : EntityDeleteOrUpdateAdapter<Module>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `modules` SET `id` = ?,`subjectId` = ?,`title` = ?,`orderIndex` = ?,`progressStatus` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Module) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.subjectId.toLong())
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.orderIndex.toLong())
        statement.bindText(5, entity.progressStatus)
        statement.bindLong(6, entity.id.toLong())
      }
    }
    this.__updateAdapterOfNote = object : EntityDeleteOrUpdateAdapter<Note>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `notes` SET `id` = ?,`moduleId` = ?,`title` = ?,`fileType` = ?,`content` = ?,`uploadedDate` = ?,`isBookmarked` = ?,`authorUsername` = ?,`department` = ?,`subject` = ?,`downloadUrl` = ?,`timestamp` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Note) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.moduleId.toLong())
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.fileType)
        statement.bindText(5, entity.content)
        statement.bindLong(6, entity.uploadedDate)
        val _tmp: Int = if (entity.isBookmarked) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindText(8, entity.authorUsername)
        statement.bindText(9, entity.department)
        statement.bindText(10, entity.subject)
        statement.bindText(11, entity.downloadUrl)
        statement.bindLong(12, entity.timestamp)
        statement.bindLong(13, entity.id.toLong())
      }
    }
    this.__updateAdapterOfAssignment = object : EntityDeleteOrUpdateAdapter<Assignment>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `assignments` SET `id` = ?,`subjectId` = ?,`title` = ?,`dueDate` = ?,`priority` = ?,`status` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Assignment) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.subjectId.toLong())
        statement.bindText(3, entity.title)
        statement.bindLong(4, entity.dueDate)
        statement.bindText(5, entity.priority)
        statement.bindText(6, entity.status)
        statement.bindLong(7, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertAnnouncement(announcement: Announcement): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAnnouncement.insert(_connection, announcement)
  }

  public override suspend fun insertSubject(subject: Subject): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfSubject.insertAndReturnId(_connection, subject)
    _result
  }

  public override suspend fun insertModule(module: Module): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfModule.insertAndReturnId(_connection, module)
    _result
  }

  public override suspend fun insertNote(note: Note): Long = performSuspending(__db, false, true) {
      _connection ->
    val _result: Long = __insertAdapterOfNote.insertAndReturnId(_connection, note)
    _result
  }

  public override suspend fun insertAssignment(assignment: Assignment): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAssignment.insert(_connection, assignment)
  }

  public override suspend fun insertExam(exam: Exam): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfExam.insert(_connection, exam)
  }

  public override suspend fun insertTimetableEntry(entry: TimetableEntry): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTimetableEntry.insert(_connection, entry)
  }

  public override suspend fun insertUserProfile(profile: UserProfile): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfUserProfile.insert(_connection, profile)
  }

  public override suspend fun insertCloudAnnouncements(announcements: List<CloudAnnouncement>): Unit
      = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCloudAnnouncement.insert(_connection, announcements)
  }

  public override suspend fun insertCloudAnnouncement(announcement: CloudAnnouncement): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCloudAnnouncement.insert(_connection, announcement)
  }

  public override suspend fun insertCommunityNotes(notes: List<CommunityNote>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCommunityNote.insert(_connection, notes)
  }

  public override suspend fun insertCommunityNote(note: CommunityNote): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCommunityNote.insert(_connection, note)
  }

  public override suspend fun insertForumPost(post: ForumPost): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfForumPost.insert(_connection, post)
  }

  public override suspend fun insertForumPosts(posts: List<ForumPost>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfForumPost.insert(_connection, posts)
  }

  public override suspend fun updateModule(module: Module): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfModule.handle(_connection, module)
  }

  public override suspend fun updateNote(note: Note): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfNote.handle(_connection, note)
  }

  public override suspend fun updateAssignment(assignment: Assignment): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfAssignment.handle(_connection, assignment)
  }

  public override fun getAllAnnouncements(): Flow<List<Announcement>> {
    val _sql: String = "SELECT * FROM announcements ORDER BY pinned DESC, timestamp DESC"
    return createFlow(__db, false, arrayOf("announcements")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPinned: Int = getColumnIndexOrThrow(_stmt, "pinned")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _result: MutableList<Announcement> = mutableListOf()
        while (_stmt.step()) {
          val _item: Announcement
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPinned: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPinned).toInt()
          _tmpPinned = _tmp != 0
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          _item =
              Announcement(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpTimestamp,_tmpPinned,_tmpImageUrl)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSubjects(department: String, semester: String): Flow<List<Subject>> {
    val _sql: String = "SELECT * FROM subjects WHERE department = ? AND semester = ?"
    return createFlow(__db, false, arrayOf("subjects")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, department)
        _argIndex = 2
        _stmt.bindText(_argIndex, semester)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSemester: Int = getColumnIndexOrThrow(_stmt, "semester")
        val _result: MutableList<Subject> = mutableListOf()
        while (_stmt.step()) {
          val _item: Subject
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCode: String?
          if (_stmt.isNull(_columnIndexOfCode)) {
            _tmpCode = null
          } else {
            _tmpCode = _stmt.getText(_columnIndexOfCode)
          }
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSemester: String
          _tmpSemester = _stmt.getText(_columnIndexOfSemester)
          _item = Subject(_tmpId,_tmpName,_tmpCode,_tmpDepartment,_tmpSemester)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSubjectById(id: Int): Flow<Subject?> {
    val _sql: String = "SELECT * FROM subjects WHERE id = ?"
    return createFlow(__db, false, arrayOf("subjects")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSemester: Int = getColumnIndexOrThrow(_stmt, "semester")
        val _result: Subject?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCode: String?
          if (_stmt.isNull(_columnIndexOfCode)) {
            _tmpCode = null
          } else {
            _tmpCode = _stmt.getText(_columnIndexOfCode)
          }
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSemester: String
          _tmpSemester = _stmt.getText(_columnIndexOfSemester)
          _result = Subject(_tmpId,_tmpName,_tmpCode,_tmpDepartment,_tmpSemester)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getModulesForSubject(subjectId: Int): Flow<List<Module>> {
    val _sql: String = "SELECT * FROM modules WHERE subjectId = ? ORDER BY orderIndex ASC"
    return createFlow(__db, false, arrayOf("modules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, subjectId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSubjectId: Int = getColumnIndexOrThrow(_stmt, "subjectId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _columnIndexOfProgressStatus: Int = getColumnIndexOrThrow(_stmt, "progressStatus")
        val _result: MutableList<Module> = mutableListOf()
        while (_stmt.step()) {
          val _item: Module
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpSubjectId: Int
          _tmpSubjectId = _stmt.getLong(_columnIndexOfSubjectId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpProgressStatus: String
          _tmpProgressStatus = _stmt.getText(_columnIndexOfProgressStatus)
          _item = Module(_tmpId,_tmpSubjectId,_tmpTitle,_tmpOrderIndex,_tmpProgressStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getNotesForModule(moduleId: Int): Flow<List<Note>> {
    val _sql: String = "SELECT * FROM notes WHERE moduleId = ? ORDER BY uploadedDate DESC"
    return createFlow(__db, false, arrayOf("notes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, moduleId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfModuleId: Int = getColumnIndexOrThrow(_stmt, "moduleId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFileType: Int = getColumnIndexOrThrow(_stmt, "fileType")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfUploadedDate: Int = getColumnIndexOrThrow(_stmt, "uploadedDate")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<Note> = mutableListOf()
        while (_stmt.step()) {
          val _item: Note
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpModuleId: Int
          _tmpModuleId = _stmt.getLong(_columnIndexOfModuleId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFileType: String
          _tmpFileType = _stmt.getText(_columnIndexOfFileType)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpUploadedDate: Long
          _tmpUploadedDate = _stmt.getLong(_columnIndexOfUploadedDate)
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              Note(_tmpId,_tmpModuleId,_tmpTitle,_tmpFileType,_tmpContent,_tmpUploadedDate,_tmpIsBookmarked,_tmpAuthorUsername,_tmpDepartment,_tmpSubject,_tmpDownloadUrl,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBookmarkedNotes(): Flow<List<Note>> {
    val _sql: String = "SELECT * FROM notes WHERE isBookmarked = 1"
    return createFlow(__db, false, arrayOf("notes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfModuleId: Int = getColumnIndexOrThrow(_stmt, "moduleId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFileType: Int = getColumnIndexOrThrow(_stmt, "fileType")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfUploadedDate: Int = getColumnIndexOrThrow(_stmt, "uploadedDate")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<Note> = mutableListOf()
        while (_stmt.step()) {
          val _item: Note
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpModuleId: Int
          _tmpModuleId = _stmt.getLong(_columnIndexOfModuleId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFileType: String
          _tmpFileType = _stmt.getText(_columnIndexOfFileType)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpUploadedDate: Long
          _tmpUploadedDate = _stmt.getLong(_columnIndexOfUploadedDate)
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              Note(_tmpId,_tmpModuleId,_tmpTitle,_tmpFileType,_tmpContent,_tmpUploadedDate,_tmpIsBookmarked,_tmpAuthorUsername,_tmpDepartment,_tmpSubject,_tmpDownloadUrl,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchNotes(query: String): Flow<List<Note>> {
    val _sql: String = "SELECT * FROM notes WHERE title LIKE '%' || ? || '%'"
    return createFlow(__db, false, arrayOf("notes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfModuleId: Int = getColumnIndexOrThrow(_stmt, "moduleId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFileType: Int = getColumnIndexOrThrow(_stmt, "fileType")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfUploadedDate: Int = getColumnIndexOrThrow(_stmt, "uploadedDate")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<Note> = mutableListOf()
        while (_stmt.step()) {
          val _item: Note
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpModuleId: Int
          _tmpModuleId = _stmt.getLong(_columnIndexOfModuleId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFileType: String
          _tmpFileType = _stmt.getText(_columnIndexOfFileType)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpUploadedDate: Long
          _tmpUploadedDate = _stmt.getLong(_columnIndexOfUploadedDate)
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              Note(_tmpId,_tmpModuleId,_tmpTitle,_tmpFileType,_tmpContent,_tmpUploadedDate,_tmpIsBookmarked,_tmpAuthorUsername,_tmpDepartment,_tmpSubject,_tmpDownloadUrl,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getNoteById(id: Int): Flow<Note?> {
    val _sql: String = "SELECT * FROM notes WHERE id = ?"
    return createFlow(__db, false, arrayOf("notes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfModuleId: Int = getColumnIndexOrThrow(_stmt, "moduleId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFileType: Int = getColumnIndexOrThrow(_stmt, "fileType")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfUploadedDate: Int = getColumnIndexOrThrow(_stmt, "uploadedDate")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: Note?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpModuleId: Int
          _tmpModuleId = _stmt.getLong(_columnIndexOfModuleId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFileType: String
          _tmpFileType = _stmt.getText(_columnIndexOfFileType)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpUploadedDate: Long
          _tmpUploadedDate = _stmt.getLong(_columnIndexOfUploadedDate)
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _result =
              Note(_tmpId,_tmpModuleId,_tmpTitle,_tmpFileType,_tmpContent,_tmpUploadedDate,_tmpIsBookmarked,_tmpAuthorUsername,_tmpDepartment,_tmpSubject,_tmpDownloadUrl,_tmpTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllNotes(): Flow<List<Note>> {
    val _sql: String = "SELECT * FROM notes ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("notes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfModuleId: Int = getColumnIndexOrThrow(_stmt, "moduleId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfFileType: Int = getColumnIndexOrThrow(_stmt, "fileType")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfUploadedDate: Int = getColumnIndexOrThrow(_stmt, "uploadedDate")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<Note> = mutableListOf()
        while (_stmt.step()) {
          val _item: Note
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpModuleId: Int
          _tmpModuleId = _stmt.getLong(_columnIndexOfModuleId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpFileType: String
          _tmpFileType = _stmt.getText(_columnIndexOfFileType)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpUploadedDate: Long
          _tmpUploadedDate = _stmt.getLong(_columnIndexOfUploadedDate)
          val _tmpIsBookmarked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp != 0
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              Note(_tmpId,_tmpModuleId,_tmpTitle,_tmpFileType,_tmpContent,_tmpUploadedDate,_tmpIsBookmarked,_tmpAuthorUsername,_tmpDepartment,_tmpSubject,_tmpDownloadUrl,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllAssignments(): Flow<List<Assignment>> {
    val _sql: String = "SELECT * FROM assignments ORDER BY dueDate ASC"
    return createFlow(__db, false, arrayOf("assignments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSubjectId: Int = getColumnIndexOrThrow(_stmt, "subjectId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDueDate: Int = getColumnIndexOrThrow(_stmt, "dueDate")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _result: MutableList<Assignment> = mutableListOf()
        while (_stmt.step()) {
          val _item: Assignment
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpSubjectId: Int
          _tmpSubjectId = _stmt.getLong(_columnIndexOfSubjectId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDueDate: Long
          _tmpDueDate = _stmt.getLong(_columnIndexOfDueDate)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item = Assignment(_tmpId,_tmpSubjectId,_tmpTitle,_tmpDueDate,_tmpPriority,_tmpStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllExams(): Flow<List<Exam>> {
    val _sql: String = "SELECT * FROM exams ORDER BY date ASC"
    return createFlow(__db, false, arrayOf("exams")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSubjectId: Int = getColumnIndexOrThrow(_stmt, "subjectId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfModulesCovered: Int = getColumnIndexOrThrow(_stmt, "modulesCovered")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: MutableList<Exam> = mutableListOf()
        while (_stmt.step()) {
          val _item: Exam
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpSubjectId: Int
          _tmpSubjectId = _stmt.getLong(_columnIndexOfSubjectId).toInt()
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpModulesCovered: String
          _tmpModulesCovered = _stmt.getText(_columnIndexOfModulesCovered)
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          _item = Exam(_tmpId,_tmpSubjectId,_tmpDate,_tmpModulesCovered,_tmpNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllTimetableEntries(): Flow<List<TimetableEntry>> {
    val _sql: String = "SELECT * FROM timetable_entries ORDER BY startTime ASC"
    return createFlow(__db, false, arrayOf("timetable_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDay: Int = getColumnIndexOrThrow(_stmt, "day")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfSubjectId: Int = getColumnIndexOrThrow(_stmt, "subjectId")
        val _columnIndexOfRoom: Int = getColumnIndexOrThrow(_stmt, "room")
        val _result: MutableList<TimetableEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: TimetableEntry
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpDay: String
          _tmpDay = _stmt.getText(_columnIndexOfDay)
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpEndTime: String
          _tmpEndTime = _stmt.getText(_columnIndexOfEndTime)
          val _tmpSubjectId: Int
          _tmpSubjectId = _stmt.getLong(_columnIndexOfSubjectId).toInt()
          val _tmpRoom: String?
          if (_stmt.isNull(_columnIndexOfRoom)) {
            _tmpRoom = null
          } else {
            _tmpRoom = _stmt.getText(_columnIndexOfRoom)
          }
          _item = TimetableEntry(_tmpId,_tmpDay,_tmpStartTime,_tmpEndTime,_tmpSubjectId,_tmpRoom)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserProfile(): Flow<UserProfile?> {
    val _sql: String = "SELECT * FROM user_profile WHERE id = 1"
    return createFlow(__db, false, arrayOf("user_profile")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUsername: Int = getColumnIndexOrThrow(_stmt, "username")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfSemester: Int = getColumnIndexOrThrow(_stmt, "semester")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfThemeMode: Int = getColumnIndexOrThrow(_stmt, "themeMode")
        val _result: UserProfile?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String?
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName)
          }
          val _tmpUsername: String
          _tmpUsername = _stmt.getText(_columnIndexOfUsername)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpSemester: String
          _tmpSemester = _stmt.getText(_columnIndexOfSemester)
          val _tmpNotificationsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp != 0
          val _tmpThemeMode: String
          _tmpThemeMode = _stmt.getText(_columnIndexOfThemeMode)
          _result =
              UserProfile(_tmpId,_tmpName,_tmpUsername,_tmpDepartment,_tmpSemester,_tmpNotificationsEnabled,_tmpThemeMode)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllCloudAnnouncements(): Flow<List<CloudAnnouncement>> {
    val _sql: String = "SELECT * FROM cloud_announcements ORDER BY pinned DESC, timestamp DESC"
    return createFlow(__db, false, arrayOf("cloud_announcements")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPinned: Int = getColumnIndexOrThrow(_stmt, "pinned")
        val _result: MutableList<CloudAnnouncement> = mutableListOf()
        while (_stmt.step()) {
          val _item: CloudAnnouncement
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPinned: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPinned).toInt()
          _tmpPinned = _tmp != 0
          _item =
              CloudAnnouncement(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpTimestamp,_tmpPinned)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllCommunityNotes(): Flow<List<CommunityNote>> {
    val _sql: String = "SELECT * FROM community_notes ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("community_notes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfModuleTitle: Int = getColumnIndexOrThrow(_stmt, "moduleTitle")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfUpvotedBy: Int = getColumnIndexOrThrow(_stmt, "upvotedBy")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfAuthorDept: Int = getColumnIndexOrThrow(_stmt, "authorDept")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _result: MutableList<CommunityNote> = mutableListOf()
        while (_stmt.step()) {
          val _item: CommunityNote
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpModuleTitle: String
          _tmpModuleTitle = _stmt.getText(_columnIndexOfModuleTitle)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthor: String
          _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpUpvotedBy: List<String>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfUpvotedBy)
          _tmpUpvotedBy = __converters.toStringList(_tmp)
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpAuthorDept: String
          _tmpAuthorDept = _stmt.getText(_columnIndexOfAuthorDept)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          _item =
              CommunityNote(_tmpId,_tmpSubject,_tmpModuleTitle,_tmpTitle,_tmpAuthor,_tmpDownloadUrl,_tmpTimestamp,_tmpUpvotedBy,_tmpAuthorUsername,_tmpAuthorDept,_tmpDepartment)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCommunityNoteById(id: String): CommunityNote? {
    val _sql: String = "SELECT * FROM community_notes WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSubject: Int = getColumnIndexOrThrow(_stmt, "subject")
        val _columnIndexOfModuleTitle: Int = getColumnIndexOrThrow(_stmt, "moduleTitle")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfUpvotedBy: Int = getColumnIndexOrThrow(_stmt, "upvotedBy")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfAuthorDept: Int = getColumnIndexOrThrow(_stmt, "authorDept")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _result: CommunityNote?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSubject: String
          _tmpSubject = _stmt.getText(_columnIndexOfSubject)
          val _tmpModuleTitle: String
          _tmpModuleTitle = _stmt.getText(_columnIndexOfModuleTitle)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpAuthor: String
          _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpUpvotedBy: List<String>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfUpvotedBy)
          _tmpUpvotedBy = __converters.toStringList(_tmp)
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpAuthorDept: String
          _tmpAuthorDept = _stmt.getText(_columnIndexOfAuthorDept)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          _result =
              CommunityNote(_tmpId,_tmpSubject,_tmpModuleTitle,_tmpTitle,_tmpAuthor,_tmpDownloadUrl,_tmpTimestamp,_tmpUpvotedBy,_tmpAuthorUsername,_tmpAuthorDept,_tmpDepartment)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllForumPosts(): Flow<List<ForumPost>> {
    val _sql: String = "SELECT * FROM forum_posts ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("forum_posts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfAuthorDept: Int = getColumnIndexOrThrow(_stmt, "authorDept")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfTag: Int = getColumnIndexOrThrow(_stmt, "tag")
        val _columnIndexOfUpvotedBy: Int = getColumnIndexOrThrow(_stmt, "upvotedBy")
        val _columnIndexOfCommentCount: Int = getColumnIndexOrThrow(_stmt, "commentCount")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ForumPost> = mutableListOf()
        while (_stmt.step()) {
          val _item: ForumPost
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpAuthorDept: String
          _tmpAuthorDept = _stmt.getText(_columnIndexOfAuthorDept)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpTag: String
          _tmpTag = _stmt.getText(_columnIndexOfTag)
          val _tmpUpvotedBy: List<String>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfUpvotedBy)
          _tmpUpvotedBy = __converters.toStringList(_tmp)
          val _tmpCommentCount: Int
          _tmpCommentCount = _stmt.getLong(_columnIndexOfCommentCount).toInt()
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              ForumPost(_tmpId,_tmpAuthorUsername,_tmpAuthorDept,_tmpDepartment,_tmpContent,_tmpTag,_tmpUpvotedBy,_tmpCommentCount,_tmpImageUrl,_tmpDownloadUrl,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getForumPostById(id: String): ForumPost? {
    val _sql: String = "SELECT * FROM forum_posts WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfAuthorDept: Int = getColumnIndexOrThrow(_stmt, "authorDept")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfTag: Int = getColumnIndexOrThrow(_stmt, "tag")
        val _columnIndexOfUpvotedBy: Int = getColumnIndexOrThrow(_stmt, "upvotedBy")
        val _columnIndexOfCommentCount: Int = getColumnIndexOrThrow(_stmt, "commentCount")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: ForumPost?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpAuthorDept: String
          _tmpAuthorDept = _stmt.getText(_columnIndexOfAuthorDept)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpTag: String
          _tmpTag = _stmt.getText(_columnIndexOfTag)
          val _tmpUpvotedBy: List<String>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfUpvotedBy)
          _tmpUpvotedBy = __converters.toStringList(_tmp)
          val _tmpCommentCount: Int
          _tmpCommentCount = _stmt.getLong(_columnIndexOfCommentCount).toInt()
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _result =
              ForumPost(_tmpId,_tmpAuthorUsername,_tmpAuthorDept,_tmpDepartment,_tmpContent,_tmpTag,_tmpUpvotedBy,_tmpCommentCount,_tmpImageUrl,_tmpDownloadUrl,_tmpTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getForumPostsByTag(tag: String): Flow<List<ForumPost>> {
    val _sql: String = "SELECT * FROM forum_posts WHERE tag = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("forum_posts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tag)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAuthorUsername: Int = getColumnIndexOrThrow(_stmt, "authorUsername")
        val _columnIndexOfAuthorDept: Int = getColumnIndexOrThrow(_stmt, "authorDept")
        val _columnIndexOfDepartment: Int = getColumnIndexOrThrow(_stmt, "department")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfTag: Int = getColumnIndexOrThrow(_stmt, "tag")
        val _columnIndexOfUpvotedBy: Int = getColumnIndexOrThrow(_stmt, "upvotedBy")
        val _columnIndexOfCommentCount: Int = getColumnIndexOrThrow(_stmt, "commentCount")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfDownloadUrl: Int = getColumnIndexOrThrow(_stmt, "downloadUrl")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ForumPost> = mutableListOf()
        while (_stmt.step()) {
          val _item: ForumPost
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAuthorUsername: String
          _tmpAuthorUsername = _stmt.getText(_columnIndexOfAuthorUsername)
          val _tmpAuthorDept: String
          _tmpAuthorDept = _stmt.getText(_columnIndexOfAuthorDept)
          val _tmpDepartment: String
          _tmpDepartment = _stmt.getText(_columnIndexOfDepartment)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpTag: String
          _tmpTag = _stmt.getText(_columnIndexOfTag)
          val _tmpUpvotedBy: List<String>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfUpvotedBy)
          _tmpUpvotedBy = __converters.toStringList(_tmp)
          val _tmpCommentCount: Int
          _tmpCommentCount = _stmt.getLong(_columnIndexOfCommentCount).toInt()
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpDownloadUrl: String
          _tmpDownloadUrl = _stmt.getText(_columnIndexOfDownloadUrl)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              ForumPost(_tmpId,_tmpAuthorUsername,_tmpAuthorDept,_tmpDepartment,_tmpContent,_tmpTag,_tmpUpvotedBy,_tmpCommentCount,_tmpImageUrl,_tmpDownloadUrl,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
