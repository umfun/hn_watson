package me.maciejb.hnanalysis.spark

import java.time.ZonedDateTime

import com.typesafe.scalalogging.LazyLogging
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

case class Author(value: String) extends AnyVal
case class StoryId(id: Int) extends AnyVal
case class CommentId(id: Int) extends AnyVal

case class Comment(author: Author, text: String, createdAt: ZonedDateTime, points: Int, storyIdOpt: Option[StoryId],
                   parentIdOpt: Option[CommentId])

object Comment extends LazyLogging {

  def optionStr(str: String) = Option(str) filter (!_.trim.isEmpty)

  implicit val JsonFormats = new DefaultFormats {}

  def fromJsonString(jsonString: String): Comment = {
    val json = parse(jsonString)
    Comment(
      Author((json \ "author").extract[String]),
      (json \ "comment_text").extract[String],
      ZonedDateTime.parse((json \ "created_at").extract[String]),
      (json \ "points").extract[Int],
      (json \ "story_id").extract[Option[String]] filter (!_.trim.isEmpty) map { idStr => StoryId(idStr.toInt) },
      (json \ "parent_id").extract[Option[String]] filter (!_.trim.isEmpty) map { idStr => CommentId(idStr.toInt) }
    )
  }

}
