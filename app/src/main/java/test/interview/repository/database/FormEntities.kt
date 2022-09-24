package test.interview.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "form")

class Form(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "number") var number: Long,
    @ColumnInfo(name = "book") var book: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}