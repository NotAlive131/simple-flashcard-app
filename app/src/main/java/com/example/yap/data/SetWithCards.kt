package com.example.yap.data

import androidx.room.Embedded
import androidx.room.Relation

data class SetWithCards(
    @Embedded val setEntity: SetEntity, // This represents the SetEntity
    @Relation(
        parentColumn = "id", // This is the column in SetEntity
        entityColumn = "setId" // This is the foreign key in CardEntity
    )
    val cards: List<cardEntity> // This is a list of CardEntity related to the SetEntity
)
