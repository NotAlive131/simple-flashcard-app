package com.example.yap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [cardEntity::class, SetEntity::class], version = 1, exportSchema = false)
abstract class CardDatabase: RoomDatabase() {

    abstract fun cardDao(): FlashcardDao
    companion object {
        @Volatile
        private var Instance: CardDatabase? = null
        fun getDatabase(context: Context): CardDatabase {
            return Instance ?: synchronized(this)
            { Room.databaseBuilder(context, CardDatabase::class.java, "cardDatabase")
                .build()
                .also{Instance = it}
            }
        }
    }
}
