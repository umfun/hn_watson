package me.maciejb.hnanalysis.spark

import java.nio.file.Paths


object ProjectPaths {
  val BasePath = Paths.get("").toAbsolutePath

  val DataDir = BasePath.resolve("data")

  val First10kCommentsJson = DataDir.resolve("hn_hits.10k.json")
  val AllCommentsJson = DataDir.resolve("hn_hits.json")

  val CommentsOutputDirectory = BasePath.resolve(Paths.get("output", "comments"))

}
