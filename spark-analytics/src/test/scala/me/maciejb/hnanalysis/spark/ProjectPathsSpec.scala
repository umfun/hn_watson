package me.maciejb.hnanalysis.spark

import java.nio.file.Files

import org.scalatest.{FlatSpec, Matchers}


class ProjectPathsSpec extends FlatSpec with Matchers {

  for (filePath <- Seq(
    ProjectPaths.First10kCommentsJson,
    ProjectPaths.AllCommentsJson
  )) {
    val fileName = filePath.getFileName.toString

    it should s"resolve $fileName to an existing file" in {
      /*
       * If fails check if JSON data is available
       */
      Files.exists(filePath) shouldBe true
    }
  }

}
