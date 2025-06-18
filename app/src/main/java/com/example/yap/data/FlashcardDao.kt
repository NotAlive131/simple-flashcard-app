package com.example.yap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: cardEntity)
    @Update
    suspend fun update(card: cardEntity)
    @Delete
    suspend fun delete(card: cardEntity)
    @Query("SELECT name FROM SetTable WHERE id = :setId")
    suspend fun getSetName(setId: Long): String
    @Upsert
    suspend fun upsertSet(set: SetEntity): Long
    @Update
    suspend fun updateSet(set: SetEntity)
    @Delete
    suspend fun deleteSet(set: SetEntity)
    @Query("SELECT * FROM SetTable WHERE id = :setId")
    suspend fun getSetById(setId: Long): SetEntity

    @Query("SELECT * FROM SetTable")
    fun getAllSets(): Flow<List<SetEntity>>
    @Query("SELECT * FROM CardTable WHERE setId = :setId")
    suspend fun getCardsForSet(setId: Long): List<cardEntity>

    // Get set by id with associated cards
    @Transaction
    @Query("SELECT * FROM SetTable WHERE id = :setId")
    suspend fun getSetWithCardsById(setId: Long): SetWithCards





}