package ru.netology.diploma.dto

data class Event(
    val attachment: Attachment?,
    val author: String?,
    val authorAvatar: String?,
    val authorId: Int?,
    val content: String?,
    val id: Int,
    val link: String?,
    val published: Int?,
    val speakerIds: List<String>?,
    val type: String?
)