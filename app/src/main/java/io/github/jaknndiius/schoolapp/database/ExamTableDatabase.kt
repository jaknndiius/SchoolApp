package io.github.jaknndiius.schoolapp.database

import androidx.room.*

@Entity
data class ExamTable(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "subjects") var subjects: List<Subject>
)

@Dao
interface ExamTableDao {
    @Query("SELECT * FROM examtable")
    fun getAll(): List<ExamTable>

    @Query("SELECT * FROM examtable WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): ExamTable

    @Insert
    fun insertAll(vararg examTables: ExamTable)

    @Update
    fun updateExamTables(vararg examTables: ExamTable)

    @Delete
    fun delete(examTable: ExamTable)
}

@Database(entities = [ExamTable::class], version = 2)
@TypeConverters(SubjectListConverts::class)
abstract class ExamTableDatabase : RoomDatabase() {
    abstract fun examTableDao(): ExamTableDao
}