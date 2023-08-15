package io.github.jaknndiius.schoolapp.database

import androidx.room.*
import com.google.gson.Gson

//@Entity
//data class Subject(
//    @PrimaryKey val name: String,
//    @ColumnInfo(name = "teacher_name") val teacherName: String?
//)


class ExamAttr(
    val questionsCount: List<Int>? = null
) {
    var ranges: List<String> = listOf()
}

class ExamAttrConverts {
    @TypeConverter
    fun examToJson(value: ExamAttr?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToExam(value: String): ExamAttr? {
        return Gson().fromJson(value, ExamAttr::class.java)
    }
}
@Entity
data class Subject(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "teacher_name") val teacherName: String?,
    @ColumnInfo(name = "exam_attr") var examAttr: ExamAttr? = null
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
@TypeConverters(ExamAttrConverts::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
}