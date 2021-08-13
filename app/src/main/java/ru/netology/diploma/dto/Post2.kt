package ru.netology.diploma.dto

data class Post2(
    val attachment: Attachment?,
    val author: String?,
    val authorAvatar: String?,
    val authorId: Int,
    val content: String?,
    val coords: String?,
    val id: Long,
    val likeOwnerIds: List<String>?,
    val likedByMe: Boolean,
    val link: String?,
    val mentionIds: List<String>?,
    val mentionedMe: Boolean,
    val published: Int
)