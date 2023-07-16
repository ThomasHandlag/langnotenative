package com.thuong.langnotenative.db

import android.content.Context
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.parcelize.Parcelize

@Database(entities = [Note::class, Word::class], version = 3, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract val appDao: AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getDatabase(context: Context): AppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "app_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

@Dao
interface AppDao {
    @Query("SELECT * FROM notes")
    fun getAllNote(): List<Note>

    @Query("SELECT * FROM notes WHERE uid = :id")
    fun getNoteById(id: String): Note

    @Query("SELECT * FROM notes WHERE name LIKE :key")
    fun findNoteByName(key: String): List<Note>

    @Query("SELECT * FROM words")
    fun getAllWord(): List<Word>

    @Query("SELECT * FROM words WHERE uid = :id")
    fun getWordById(id: String): Word

    @Query("SELECT * FROM words WHERE word LIKE :key")
    fun findWord(key: String): List<Word>

    @Query("DELETE FROM notes WHERE uid = :id")
    fun deleteNoteById(id: String)

    @Query("DELETE FROM words WHERE uid = :id")
    fun deleteWordById(id: String)

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun deleteWord(word: Word)

    @Insert
    fun insertWord(word: Word)

    @Delete
    fun deleteNote(note: Note)

    @Update
    fun updateWord(word: Word)

    @Update
    fun updateNote(note: Note)
}
@Parcelize
@Entity("notes")
data class Note(
    @PrimaryKey var uid: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "cont") var cont: String = "",
    @ColumnInfo(name = "date") var date: String = ""
) : Parcelable

@Parcelize
@Entity("words")
data class Word(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "word") var word: String,
    @ColumnInfo(name = "mean") var mean: String,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "eg") var eg: String,
    @ColumnInfo(name = "date") var date: String
) : Parcelable
