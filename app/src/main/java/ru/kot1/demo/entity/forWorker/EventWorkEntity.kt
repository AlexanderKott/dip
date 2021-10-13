package ru.kot1.demo.entity.forWorker

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kot1.demo.dto.Attachment
import ru.kot1.demo.dto.Coords
import ru.kot1.demo.dto.Event
import ru.kot1.demo.dto.Post

@Entity
data class EventWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String?,
    val authorAvatar: String?,
    val authorId: Int,
    val content: String?,
    val link: String?,
    val published: Long,
    val datetime : Long,
    val speakerIds: List<String>?,
    @ColumnInfo(name = "event_type")
    val type: String?,
    @Embedded
    val attachment: Attachment?,
    val mediaUri: String?,
    val mediaType: String?,
) {
    fun toDto() = Event(attachment, author, authorAvatar,authorId, content, id, link,
        published, datetime, speakerIds, type)

    companion object {
        fun fromDto(dto: Event) =
            EventWorkEntity(dto.id,dto.author, dto.authorAvatar, dto.authorId, dto.content, dto.link,
                dto.published, dto.datetime,dto.speakerIds,dto.type, dto.attachment,
                 "", "")
    }
}