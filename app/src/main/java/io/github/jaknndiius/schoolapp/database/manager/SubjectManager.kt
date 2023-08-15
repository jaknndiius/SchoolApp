package io.github.jaknndiius.schoolapp.database.manager

import io.github.jaknndiius.schoolapp.database.ExamAttr
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.database.SubjectDao

class SubjectManager(
    private val subjectDao: SubjectDao
) {

    fun clear() {
        subjectDao.getAll().forEach { subjectDao.delete(it) }
    }

    fun getAll(): List<Subject> {
        return subjectDao.getAll()
    }

    fun get(name: String): Subject {
        return subjectDao.getAll().first { it.name == name }
    }

    fun isExist(name: String): Boolean {
        return !subjectDao.getAll().none { it.name == name }
    }

    fun define(name: String, teacherName: String) {
        if (isExist(name)) subjectDao.updateSubjects(Subject(name, teacherName))
        else subjectDao.insertAll(Subject(name, teacherName))
    }

    fun attachExam(name: String, examAttr: ExamAttr) {
        if (!isExist(name)) return
        subjectDao.updateSubjects(get(name).copy(examAttr = examAttr))
    }

    fun detachExam(name: String) {
        if (!isExist(name)) return
        subjectDao.updateSubjects(get(name).copy(examAttr = null))
    }

    fun delete(name: String) {
        if (!isExist(name)) return
        subjectDao.delete(get(name))
    }

}