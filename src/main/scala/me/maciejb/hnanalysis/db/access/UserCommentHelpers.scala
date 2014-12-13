package me.maciejb.hnanalysis.db.access

import com.mongodb.casbah.commons.MongoDBObject

/**
 * @author Maciej Bilas
 * @since 22/11/14 12:13
 */
trait UserCommentHelpers {

  def userId(e: MongoDBObject) = e.getAs[String]("_id")

}