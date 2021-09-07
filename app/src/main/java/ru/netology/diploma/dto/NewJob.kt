package ru.netology.diploma.dto

data class NewJob(
    val id: Int = 0,
    val name: String,
    val finish: Long,
    val position: String,
    val start: Long
)