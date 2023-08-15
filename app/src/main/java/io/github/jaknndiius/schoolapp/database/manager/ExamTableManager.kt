package io.github.jaknndiius.schoolapp.database.manager

import io.github.jaknndiius.schoolapp.database.ExamTable
import io.github.jaknndiius.schoolapp.database.ExamTableDao
import io.github.jaknndiius.schoolapp.database.Subject

class ExamTableManager(
    private val examTableDao: ExamTableDao,
    private val subjectManager: SubjectManager
){
    fun getAll(): List<ExamTable> {
        return examTableDao.getAll()
    }
    private fun findByName(dayName: String): ExamTable {
        return examTableDao.findByName(dayName)
    }
    fun get(dayName: String): ExamTable {
        return if(isExist(dayName)) findByName(dayName) else ExamTable(dayName, listOf())
    }
    fun isExist(dayName: String): Boolean {
        return !getAll().none { it.name == dayName }
    }
    fun addOrUpdate(dayName: String, subjectNames: List<String>) {
        val subjects = arrayListOf<Subject>()
        subjectNames.forEach {
            if(subjectManager.isExist(it)) subjects.add(subjectManager.get(it))
        }
        if(isExist(dayName)) examTableDao.updateExamTables(ExamTable(dayName, subjects))
        else examTableDao.insertAll(ExamTable(dayName, subjects))
    }
    fun delete(dayName: String) {
        if(!isExist(dayName)) return
        examTableDao.delete(findByName(dayName))
    }
}