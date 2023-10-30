package io.github.jaknndiius.schoolapp.database

import androidx.room.*
import com.google.gson.Gson
import io.github.jaknndiius.schoolapp.preset.RangeType

class ExamAttr(
    val questionsCount: List<Int>? = null
) {
    var ranges: List<Pair<RangeType, String>> = listOf()

    fun convert() = ConvertedExamAttr(
        questionsCount,
        ranges.map { pair ->
            pair.first.name to pair.second
        }
    )
}

class ConvertedExamAttr(
    private val questionsCount: List<Int>?,
    private val convertedRanges: List<Pair<String, String>>
) {
    fun restore() = ExamAttr(questionsCount).apply {
        ranges = convertedRanges.map { pair ->
            RangeType.valueOf(pair.first) to pair.second
        }
    }
}

class ExamAttrConverts {
    @TypeConverter
    fun examToJson(value: ExamAttr?): String? {
        return value?.let { Gson().toJson(value.convert()) }
    }

    @TypeConverter
    fun jsonToExam(value: String?): ExamAttr? {
        return value?.let { Gson().fromJson(value, ConvertedExamAttr::class.java).restore() }
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