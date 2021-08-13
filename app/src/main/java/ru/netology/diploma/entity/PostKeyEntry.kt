package ru.netology.diploma.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostKeyEntry(
    @PrimaryKey
    val type: Type,
    val id: Long
)
{
    enum class Type {
        PREPEND,
        APPEND
    }

}