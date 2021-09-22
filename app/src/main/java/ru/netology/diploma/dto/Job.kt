package ru.netology.diploma.dto

data class Job(
    val id: Long,
    val authorId: Long,
    val name: String,
    val position: String,
    val start: Long,
    val finish: Long? = null,
    val link: String? = null,
    val belongsToMe : Boolean = false
)