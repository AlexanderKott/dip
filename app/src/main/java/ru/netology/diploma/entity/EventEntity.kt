package ru.netology.diploma.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.Event

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded
    val attachment: Attachment?,
    val author: String?,
    val authorAvatar: String?,
    val authorId: Int,
    val content: String?,
    val link: String?,
    val published: Long,
    val speakerIds: List<String>?,
    @ColumnInfo(name = "event_type")
    val type: String?
){
    fun toDto() = Event(
        attachment,
        author,
        authorAvatar,
        authorId,
        content,
        id,
        link,
        published,
        speakerIds,
        type
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(dto.id,
                dto.attachment,
                dto.author ,
                dto.authorAvatar ,
                dto.authorId ,
                dto.content,
                dto.link,
                dto.published,
                dto.speakerIds,
                dto.type )
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)
