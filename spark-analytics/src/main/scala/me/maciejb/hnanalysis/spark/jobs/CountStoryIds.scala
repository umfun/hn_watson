package me.maciejb.hnanalysis.spark.jobs

import me.maciejb.hnanalysis.spark.{Comment, ProjectPaths}


object CountStoryIds extends App {

  import me.maciejb.hnanalysis.spark.HnSparkContextProvider._

  lazy val data = sparkContext.textFile(ProjectPaths.AllCommentsJson.toString).cache()

  def transform() = {
    val commentRdd = data.map(Comment.fromJsonString).filter(_.storyIdOpt.isDefined)
    val groupedByStoryId = commentRdd.groupBy(_.storyIdOpt.get).map { case (storyId, comments) =>
      (storyId, comments.size)
    }

    val sorted = groupedByStoryId.sortBy({ case (sId, commentCount) => commentCount }, ascending = false)

    sorted.take(10)
  }

  for (elem <- transform()) {
    println(elem)
  }

}
