package me.maciejb.hnanalysis.db.access

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

import me.maciejb.hnanalysis.db.UserWithComments

class UserCommentsDao(db: MongoDB) extends UserCommentHelpers {
  protected val coll = db("user_comments")

  def commentsOf(author: String): Option[UserWithComments] = for {
    e <- coll.findOneByID(author)
    comments <- e.getAs[Seq[String]]("comments")
  } yield UserWithComments(author, comments)

  def topUsers(n: Int): Seq[String] = {
    for {
      e <- coll.find().sort(MongoDBObject("comments_count" -> -1)).limit(n)
      id <- userId(e)
    } yield id
  }.toSeq

}
