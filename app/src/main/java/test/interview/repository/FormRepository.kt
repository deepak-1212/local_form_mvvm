package test.interview.repository

import androidx.lifecycle.LiveData
import test.interview.repository.database.Form
import test.interview.repository.database.FormDao

class FormRepository(private val formDao: FormDao) {

    val formData: LiveData<Form> = formDao.getForm()

    suspend fun insert(note: Form) {
        formDao.insert(note)
    }

    suspend fun delete(note: Form){
        formDao.delete(note)
    }

    suspend fun update(note: Form){
        formDao.update(note)
    }
}