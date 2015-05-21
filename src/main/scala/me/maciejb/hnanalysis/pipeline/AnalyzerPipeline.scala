package me.maciejb.hnanalysis.pipeline

import akka.actor.ActorSystem
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.softwaremill.thegarden.lawn.control.retry
import me.maciejb.hnanalysis.db.access.UserCommentsDao
import me.maciejb.hnanalysis.submission.{StoriesQualifier, Submitter, WatsonRespExtractor}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try

class AnalyzerPipeline(submitter: Submitter, userCommentsDao: UserCommentsDao)
                      (implicit protected val system: ActorSystem,
                       protected val ec: ExecutionContext) extends LazyLogging {

  def profileOf(user: String): Option[String] = {
    val qualifiedOpt = for {
      comments <- userCommentsDao.commentsOf(user)
    } yield StoriesQualifier.qualified(comments.concatenated)

    qualifiedOpt.flatMap { qualified =>
      logger.info(s"Submitting profile of $user to Watson.")
      val sOpt = retry()(() => {Try {Await.result(submitter.submitText(qualified), 70.seconds)}.toOption})
      sOpt.flatMap { s => WatsonRespExtractor.profileOf(user, s)}
    }
  }

}
