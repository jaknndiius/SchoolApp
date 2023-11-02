package io.github.jaknndiius.schoolapp.database

import androidx.room.*
import com.google.gson.Gson
import io.github.jaknndiius.schoolapp.preset.RangeType

@Entity
data class Schedule(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "detail") val detail: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "display_date") var displayDate: String,
    @ColumnInfo(name = "class_number") var classNumber: Int
)
@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule")
    fun getAll(): List<Schedule>

    @Query("SELECT * FROM schedule WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Schedule

    @Insert
    fun insertAll(vararg schedule: Schedule)

    @Update
    fun updateSchedules(vararg schedules: Schedule)

    @Delete
    fun delete(schedule: Schedule)
}

@Database(entities = [Schedule::class], version = 1)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}