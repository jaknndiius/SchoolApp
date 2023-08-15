package io.github.jaknndiius.schoolapp.database.manager

import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.database.SubjectTable
import io.github.jaknndiius.schoolapp.database.SubjectTableDao
import io.github.jaknndiius.schoolapp.database.WeekDay

class SubjectTableManager(
    private val subjectTableDao: SubjectTableDao,
    private val subjectManager: SubjectManager
) {
    fun clear() {
        subjectTableDao.getAll().forEach { subjectTableDao.delete(it)  }
    }
    fun getAll(): List<SubjectTable> {
        val newList: ArrayList<SubjectTable> = arrayListOf()
        for(weekDay in WeekDay.values()) {
            if(isExist(weekDay)) newList.add(findByWeekday(weekDay))
            else newList.add(SubjectTable(weekDay, listOf()))
        }
        return newList
    }
    private fun findByWeekday(weekDay: WeekDay): SubjectTable {
        return subjectTableDao.findById(weekDay)
    }
    fun get(weekDay: WeekDay): SubjectTable {
        return getAll()[weekDay.ordinal]
    }
    private fun isExist(weekDay: WeekDay): Boolean {
        return !subjectTableDao.getAll().none { it.uid == weekDay }
    }
    fun addOrUpdate(weekDay: WeekDay, subjectNames: List<String>) {
        val subjects = arrayListOf<Subject>()
        subjectNames.forEach {
            if(subjectManager.isExist(it)) subjects.add(subjectManager.get(it))
        }
        if(isExist(weekDay)) subjectTableDao.updateSubjectTables(SubjectTable(weekDay, subjects))
        else subjectTableDao.insertAll(SubjectTable(weekDay, subjects))
    }
    fun delete(weekDay: WeekDay) {
        if(!isExist(weekDay)) return
        subjectTableDao.delete(findByWeekday(weekDay))
    }
}