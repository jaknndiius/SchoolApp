package io.github.jaknndiius.schoolapp.database

import androidx.room.*

@Entity
data class Subject(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "teacher_name") val teacherName: String?
)
@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject")
    fun getAll(): List<Subject>

    @Query("SELECT * FROM subject WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Subject

    @Insert
    fun insertAll(vararg subjects: Subject)

    @Update
    fun updateSubjects(vararg subjects: Subject)

    @Delete
    fun delete(subject: Subject)
}

@Database(entities = [Subject::class], version = 1)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
}