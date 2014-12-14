package me.maciejb.hnanalysis.infrastructure

import java.nio.file.Paths

import akka.actor.ActorSystem
import com.softwaremill.thegarden.lawn.shutdownables.DefaultShutdownHandlerModule
import me.maciejb.hnanalysis.db.access.UserCommentsDao
import me.maciejb.hnanalysis.db.transformations._
import me.maciejb.hnanalysis.pipeline.AnalyzerPipeline
import me.maciejb.hnanalysis.submission.Submitter

trait ConfigurationModule {

  val outputDirectory = Paths.get("/", "Users", "maciejb", "Development", "hn_watson", "output")
  val top10OutputDirectory = outputDirectory.resolve("top10")
  val top10ProfilesOutputDirectory = outputDirectory.resolve("top10profiles")
  val userCommentsOutputDirectory = outputDirectory.resolve("users")
  val userCommentsOutputProfileDirectory = outputDirectory.resolve("users_profiles")
}

trait MongoDBModule {

  import com.mongodb.casbah.Imports._

  lazy val mongoClient = MongoClient("localhost", 27017)
  lazy val db: MongoDB = mongoClient("hackernews")

  lazy val commentsColl: MongoCollection = db("comments")
  lazy val sampleColl: MongoCollection = db("c_sample")
  lazy val hitsColl: MongoCollection = db("hits")
}

trait DataAccessModule extends MongoDBModule {
  lazy val userCommentsDao = new UserCommentsDao(db)
}

trait TransformersModule extends MongoDBModule with ConfigurationModule {

  lazy val inputTransformer = new InputTransformers(commentsColl, sampleColl, hitsColl)
  lazy val countElementsInHits = new CountElementsInHits(db)

}

trait InfrastructureModule extends DefaultShutdownHandlerModule {
  implicit lazy val system = ActorSystem("hn_analysis").onShutdown { s =>
    s.shutdown()
    s.awaitTermination()
  }

  implicit lazy val executionContext = scala.concurrent.ExecutionContext.Implicits.global
}

trait SubmissionModule extends InfrastructureModule {
  lazy val submitter = new Submitter()
}

trait PipelineModule extends SubmissionModule with InfrastructureModule with DataAccessModule {
  lazy val analyzerPipeline = new AnalyzerPipeline(submitter, userCommentsDao)
}

object Beans extends TransformersModule with PipelineModule
