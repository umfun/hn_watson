package me.maciejb.hnanalysis.db.transformations

import java.nio.file.Path
import java.util.concurrent.CountDownLatch

import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoCollection, ParallelScanOptions}
import com.typesafe.scalalogging.slf4j.LazyLogging
import me.maciejb.hnanalysis.db.UserWithComments
import me.maciejb.hnanalysis.db.access.UserCommentHelpers
import me.maciejb.hnanalysis.infrastructure.Beans

import scala.concurrent.Future

// https://news.ycombinator.com/leaders
// http://watson-um-demo.mybluemix.net


class UserCommentsExtractor(db: MongoDB, userCommentsDir: Path) extends UserCommentHelpers {
  val coll = db("user_comments")

  @deprecated("Use UserCommentsDao.commentsOf", "2014-12-13")
  def extract(author: String): Unit = {
    val userCommentsOpt: Option[UserWithComments] =
      for (
        e <- coll.findOneByID(author);
        comments <- e.getAs[Seq[String]]("comments")
      ) yield UserWithComments(author, comments)

    for (userComments <- userCommentsOpt) {userComments.writeToFile(userCommentsDir)}
  }

}

object CountElementsInHitsRunner extends App {
  Beans.countElementsInHits.countHits()
}

class CountElementsInHits(db: MongoDB) extends LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits._

  val coll = db("user_comments")

  private var bulkOperationBuilder: BulkWriteOperation = coll.initializeUnorderedBulkOperation
  private var operationsInBulkBuilder: Int = 0

  val fourFuturesLatch = new CountDownLatch(4)

  var missingIdCount = 0

  def countHits() = {
    val cursors = coll.parallelScan(ParallelScanOptions(4, 100))

    for (cursor <- cursors) {
      Future {
        cursor.foreach { e =>
          try {
            if (e.getAs[String]("_id").isEmpty) {
              missingIdCount += 1
            }
            for (id <- e.getAs[String]("_id")) {
              val comments = e.getAsOrElse[Seq[String]]("comments", Nil)
              val commentsCount = comments.length
              write(id, commentsCount)
            }
          } catch {
            case e: Exception =>
              e.printStackTrace()
              throw e
          }
        }
        fourFuturesLatch.countDown()
      }
    }

    fourFuturesLatch.await()

    println(s"Missing id count $missingIdCount")

    this.synchronized {
      if (operationsInBulkBuilder > 0)
        bulkOperationBuilder.execute()
    }
  }

  def write(id: String, commentsLength: Int) = {
    this.synchronized {
      bulkOperationBuilder.find(MongoDBObject("_id" -> id)).updateOne($set("comments_count" -> commentsLength))
      operationsInBulkBuilder += 1

      if (operationsInBulkBuilder > 10000) {
        try {
          println("Writing batch...")
          val bob = bulkOperationBuilder
          bob.execute()
          operationsInBulkBuilder = 0
        } finally {
          bulkOperationBuilder = coll.initializeUnorderedBulkOperation
        }
      }
    }

  }

}

class InputTransformers(commentsColl: MongoCollection,
                        sampleColl: MongoCollection,
                        hitsColl: MongoCollection) extends LazyLogging {

  var transformed = 0L

  def copyOneToSample(): Unit = {
    commentsColl.findOne() match {
      case Some(firstDoc) =>
        sampleColl.insert(firstDoc)
      case None =>
        logger.error("Could not obtain first doc.")
    }
  }

  def transformAll() = {
    logger.info("Transforming...")
    commentsColl.find() foreach { e =>
      transformSingle(e)
    }
    logger.info("Done")
  }

  def transformSingle(doc: DBObject): Unit = {

    doc.getAsOrElse[Seq[DBObject]]("hits", Nil) map { hit =>
      transformed += 1
      hitsColl.insert(hit)

      if (transformed % 1000 == 0)
        println(s"Transformed $transformed entries.")
    }
  }

}
