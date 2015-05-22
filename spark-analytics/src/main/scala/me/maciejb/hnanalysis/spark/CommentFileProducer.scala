package me.maciejb.hnanalysis.spark

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import org.apache.spark.{SparkConf, SparkContext}
import org.jsoup.Jsoup

object HnSparkContextProvider {

  lazy val sparkContext: SparkContext = {
    val sparkConf = new SparkConf().
      setMaster("local[*]").
      setAppName("HnAnalysis").
      set("spark.executor.memory", "8g")

    new SparkContext(sparkConf)
  }

}

class CommentFileProducer(dataPath: Path) {

  import HnSparkContextProvider._

  lazy val data = sparkContext.textFile(dataPath.toString, 2).cache()

  private def concatenateComments(comments: Seq[Comment]): String = {
    val commentTexts = for (comment <- comments) yield {
      val rawText = comment.text
      Jsoup.parse(rawText).body().text()
    }
    commentTexts.mkString("\n")
  }

  def transform() = {
    val mapped = data.map(line => Comment.fromJsonString(line)).cache()
    val counts = mapped.groupBy(_.author).map { case (author, comments) =>
      (author, comments, comments.count(_ => true))
    }
    val sorted = counts.sortBy({ case (author, comments, count) => count}, ascending = false)

    for (view <- sorted.take(10).map { case (author, comments, count) => CommentsView(author, comments.toSeq, count)}) {
      val filePath = ProjectPaths.CommentsOutputDirectory.resolve(s"${view.author.value}.txt")
      println(filePath)
      Files.write(filePath, concatenateComments(view.comments).getBytes(Charset.defaultCharset()))
    }

  }

}

case class CommentsView(author: Author, comments: Seq[Comment], count: Int)

object CommentsToCommentFilesTransformerApp extends App {
  new CommentFileProducer(ProjectPaths.AllCommentsJson).transform()
}
