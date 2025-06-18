package com.example.yap.ui.theme
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yap.data.SetEntity

import com.example.yap.data.cardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CardViewModel(private val repository: Repository) : ViewModel() {
    val allSets: Flow<List<SetEntity>> = repository.allSets
    var currentSetId: Long by mutableLongStateOf(0)
    private val _cards = MutableStateFlow<List<cardEntity>>(emptyList())
    val cards: Flow<List<cardEntity>> = _cards
    private val _setName = mutableStateOf<String?>(null)
    val setName: State<String?> = _setName
    private val _setEntity = mutableStateOf<SetEntity?>(null)
    val setEntity: State<SetEntity?> = _setEntity

    suspend fun addSet(set: SetEntity):Long
    {
        return repository.insertSet(set)
    }
    fun EditSet(set: SetEntity)
    {
        viewModelScope.launch { repository.EditSet(set) }

    }
    fun addCard(card: cardEntity)
    {
        viewModelScope.launch{
            repository.insertCard(card)
        }
    }
    fun deleteSet(set: SetEntity)
    {
        viewModelScope.launch { repository.deleteSet(set) }
    }
    fun getSet(setId: Long)
    {
       viewModelScope.launch { _setEntity.value =  repository.getSet(setId) }
    }
    fun deleteCard(card: cardEntity)
    {
        viewModelScope.launch { repository.deleteCard(card)
            _cards.value = _cards.value.filter { it != card }}

    }

    fun editCard(card: cardEntity) {
        viewModelScope.launch {
            repository.updateCard(card)
            _cards.value = _cards.value.map {
                if (it.cardId == card.cardId) card else it
            }
        }
    }

    fun getCardsForSet(setId: Long) {
        viewModelScope.launch {
            val result = repository.getCardsBySets(setId)
            val sortedCards = result.sortedByDescending { priority(it) }
            _cards.value = sortedCards
        }
    }


    fun getSetName(setId: Long) {
        viewModelScope.launch {
            _setName.value = repository.getSetName(setId)
        }
    }

    fun pickCardWeighted(cards: List<cardEntity>): cardEntity? {
        if (cards.isEmpty()) return null
        val priorities = cards.map { priority(it).coerceAtLeast(0.0001) }
        val totalWeight = priorities.sum()
        val rand = Math.random() * totalWeight
        var cumulative = 0.0
        for (i in cards.indices) {
            cumulative += priorities[i]
            if (rand <= cumulative) return cards[i]
        }
        return cards.last()
    }

    private fun priority(card: cardEntity): Double {
        val now = System.currentTimeMillis()
        val day_in_millis = 24 * 60 * 60 * 1000
        val dueDate = card.lastReviewedDate + card.interval * day_in_millis
        val timeUntilDue = dueDate - now
        val overdueFactor = if (timeUntilDue <= 0) 1.0 else 1.0 / (timeUntilDue.toDouble() + 1)
        val easinessFactor = 3.0 - card.easiness  // Lower easiness means harder card => higher factor
        return overdueFactor * easinessFactor
    }




}