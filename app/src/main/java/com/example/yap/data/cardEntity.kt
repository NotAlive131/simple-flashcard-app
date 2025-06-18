package com.example.yap.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "CardTable",
    foreignKeys = [ForeignKey(entity = SetEntity::class,
        parentColumns = ["id"],
        childColumns = ["setId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class cardEntity (
    val front: String,
    val back: String,

    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0,

    var setId: Long,

    // Spaced repetition fields
    var lastReviewedDate: Long = 0L,  // timestamp of last review
    var interval: Int = 0,            // days until next review
    var repetition: Int = 0,          // how many times repeated successfully
    var easiness: Double = 2.5        // easiness factor for SM-2, default 2.5
)
