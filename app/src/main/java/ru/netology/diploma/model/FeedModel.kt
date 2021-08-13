package ru.netology.diploma.model

import ru.netology.diploma.dto.Event
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.Post2

sealed interface FeedModel {
    val id: Long

  }

data class  PostModel(
    val post: Post2
) : FeedModel {
    override  val id: Long = post.id
}

data class  AdModel(override  val id: Long,
val picture : String
) : FeedModel
