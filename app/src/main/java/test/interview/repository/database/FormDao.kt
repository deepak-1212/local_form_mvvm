package test.interview.repository.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FormDao {

    // below is the insert method for
    // adding a new entry to our database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note : Form)

    // below is the delete method
    // for deleting our note.
    @Delete
    suspend fun delete(form: Form)

    // below is the method to read all the notes
    // from our database we have specified the query for it.
    // inside the query we are arranging it in ascending
    // order on below line and we are specifying
    // the table name from which
    // we have to get the data.
    @Query("Select * from form order by id DESC LIMIT 1")
    fun getForm(): LiveData<Form>

    // below method is use to update the note.
    @Update
    suspend fun update(form: Form)

}