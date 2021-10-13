package ru.kot1.demo.dto

data class Event(
    val attachment: Attachment?,
    val author: String?,
    val authorAvatar: String?,
    val authorId: Int,
    val content: String?,
    val id: Int,
    val link: String?,
    val published: Long,
    val datetime : Long,
    val speakerIds: List<String>?,
    val type: String?
)


val emptyEvent = Event (
    attachment = null,
    author = null,
    authorAvatar = null,
    authorId = 0,
    content = null,
    id = 0,
    link = null,
    published = 0,
    datetime = 0,
    speakerIds = null,
    type = null
        )