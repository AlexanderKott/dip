package ru.netology.diploma.entity

import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.enumeration.AttachmentType

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type.name)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, AttachmentType.valueOf(it.type))
        }
    }
}


