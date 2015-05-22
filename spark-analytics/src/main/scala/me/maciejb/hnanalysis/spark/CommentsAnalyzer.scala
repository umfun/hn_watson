package me.maciejb.hnanalysis.spark

import java.nio.file.Path

import org.apache.spark.{SparkConf, SparkContext}

import scala.util.{Failure, Try}


object WordCountApp extends App {
  import ProjectPaths._

  val dataPath = First10kCommentsJson

  val analyzer = new CommentsAnalyzer(dataPath)

  analyzer.printParsed()

}

class CommentsAnalyzer(dataPath: Path) {

  lazy val sparkContext: SparkContext = {
    val sparkConf = new SparkConf().
      setMaster("local[*]").
      //      setMaster("spark://quoll.local:7077").
      setAppName("WordCount").
      set("spark.executor.memory", "8g")

    new SparkContext(sparkConf)
  }


  lazy val data = sparkContext.textFile(dataPath.toString, 2).cache()

  def count(): Long = data.count()

  def printFailure(f: Try[_]) = f match {
    case Failure(ParsingException(line, _)) => println(line)
    case Failure(f: Throwable) => f.printStackTrace()
    case _ => ()
  }

  def printParsed(): Unit = {
    val mapped = data.map(line => Comment.fromJsonString(line)).cache()
    val counts = mapped.groupBy(_.author).map { case (author, comments) => (author, comments.count(_ => true)) }
    val sorted = counts.sortBy({ case (author, commentCount) => commentCount }, ascending = false)

    println(sorted.take(10).mkString("\n"))
  }

}
