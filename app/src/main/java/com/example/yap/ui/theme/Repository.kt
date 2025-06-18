package com.example.yap.ui.theme

import com.example.yap.data.FlashcardDao
import com.example.yap.data.SetEntity
import com.example.yap.data.cardEntity
import kotlinx.coroutines.flow.Flow

class Repository(private val flashcardDao: FlashcardDao) {
    val allSets: Flow<List<SetEntity>> = flashcardDao.getAllSets()

    suspend fun getCardsBySets(setId: Long) : List<cardEntity>
    {
        return flashcardDao.getCardsForSet(setId)
    }
    suspend fun insertSet(set: SetEntity): Long
    {
        val id = flashcardDao.upsertSet(set)
        return id
    }
    suspend fun EditSet(set: SetEntity)
    {
       flashcardDao.updateSet(set)
    }
    suspend fun deleteSet(set: SetEntity)
    {
        flashcardDao.deleteSet(set)

    }
    suspend fun getSet(setId: Long): SetEntity
    {
        return flashcardDao.getSetById(setId)
    }
    suspend fun insertCard(card: cardEntity)
    {
        flashcardDao.insert(card)
    }
    suspend fun updateCard(card: cardEntity)
    {
        flashcardDao.update(card)
    }
    suspend fun deleteCard(card: cardEntity)
    {
        flashcardDao.delete(card)
    }
    suspend fun getSetName(setId: Long):String
    {
        return flashcardDao.getSetName(setId)
    }

}