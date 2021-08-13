package ru.netology.diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.Post2

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String?,
    val authorAvatar: String?,
    val authorId: Int,
    val content: String?,
    val coords: String?,
    val likeOwnerIds: List<String>?,
    val likedByMe: Boolean,
    val link: String?,
    val mentionIds: List<String>?,
    val mentionedMe: Boolean,
    val published: Int,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post2(attachment?.toDto(), author, authorAvatar,authorId, content, coords, id,
        likeOwnerIds, likedByMe, link, mentionIds, mentionedMe, published)

    companion object {
        fun fromDto(dto: Post2) =
            PostEntity(dto.id,dto.author, dto.authorAvatar,dto.authorId,dto.content, dto.coords,
                dto.likeOwnerIds,dto.likedByMe,dto.link,dto.mentionIds, dto.mentionedMe,
                dto.published, AttachmentEmbeddable.fromDto(dto.attachment) )
    }
}