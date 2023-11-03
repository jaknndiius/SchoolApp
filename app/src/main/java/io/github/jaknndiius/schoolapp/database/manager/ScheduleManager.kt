package io.github.jaknndiius.schoolapp.database.manager

import io.github.jaknndiius.schoolapp.database.*

class ScheduleManager(
    private val scheduleDao: ScheduleDao
) {

    fun clear() {
        scheduleDao.getAll().forEach { scheduleDao.delete(it) }
    }

    fun getAll(): List<Schedule> {
        return scheduleDao.getAll()
    }

    fun get(id: Long): Schedule {
        return scheduleDao.getAll().first { it.id == id }
    }

    private fun isExist(id: Long): Boolean {
        return !scheduleDao.getAll().none { it.id == id }
    }

    fun add(schedule: Schedule) {
        if(!isExist(schedule.id)) scheduleDao.insertAll(schedule)
    }

    fun modify(schedule: Schedule) {
        if(isExist(schedule.id)) scheduleDao.updateSchedules(schedule)
    }

    fun delete(id: Long) {
        if (!isExist(id)) return
        scheduleDao.delete(get(id))
    }

}