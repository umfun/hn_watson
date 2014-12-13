package me.maciejb.hnanalysis.pipeline

import java.nio.charset.Charset
import java.nio.file.{StandardOpenOption, OpenOption, Files}

import akka.actor.ActorSystem
import com.typesafe.scalalogging.slf4j.LazyLogging
import me.maciejb.hnanalysis.db.access.UserCommentsDao
import me.maciejb.hnanalysis.infrastructure.Beans
import me.maciejb.hnanalysis.submission.{WatsonRespExtractor, StoriesQualifier, Submitter}

import scala.concurrent.{Await, ExecutionContext}

import scala.concurrent.duration._

import me.maciejb.hnanalysis.control.retry

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


