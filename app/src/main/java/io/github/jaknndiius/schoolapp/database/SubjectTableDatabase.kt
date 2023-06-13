package io.github.jaknndiius.schoolapp.database

import androidx.room.*
import com.google.gson.Gson

enum class WeekDay {
    MONDAY, TUESDAY, WENDESDAY, THURSDAY, FRIDAY
}

class SubjectListConverts {
    @TypeConverter
    fun listToJson(value: List<Subject>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<Subject>? {
        return Gson().fromJson(value, Array<Subject>::class.java)?.toList()
    }
}

@Entity
data class SubjectTable(
    @PrimaryKey val uid: WeekDay,
    @ColumnInfo(name = "subjects") val subjects: List<Subject>
)

@Dao
interface SubjectTableDao {
    @Query("SELECT * FROM subjecttable")
    fun getAll(): List<SubjectTable>

    @Query("SELECT * FROM subjecttable WHERE subjects LIKE :subjects LIMIT 1")
    fun findByName(subjects: List<Subject>): SubjectTable

    @Insert
    fun insertAll(vararg subjectTables: SubjectTable)

    @Update
    fun updateSubjectTables(vararg subjectTables: SubjectTable)

    @Delete
    fun delete(subjectTable: SubjectTable)
}

@Database(entities = [SubjectTable::class], version = 1)
@TypeConverters(SubjectListConverts::class)
abstract class SubjectTableDatabase : RoomDatabase() {
    abstract fun subjectTableDao(): SubjectTableDao
}