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

    fun get(name: String): Schedule {
        return scheduleDao.getAll().first { it.name == name }
    }

    fun isExist(name: String): Boolean {
        return !scheduleDao.getAll().none { it.name == name }
    }

    fun define(schedule: Schedule) {
        if (isExist(schedule.name)) scheduleDao.updateSchedules(schedule)
        else scheduleDao.insertAll(schedule)
    }

    fun delete(name: String) {
        if (!isExist(name)) return
        scheduleDao.delete(get(name))
    }

}