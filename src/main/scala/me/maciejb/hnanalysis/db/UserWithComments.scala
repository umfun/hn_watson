package me.maciejb.hnanalysis.db

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import org.jsoup.Jsoup

import scala.util.Try

case class UserWithComments(id: String, comments: Seq[String]) {

  lazy val concatenated: String = Jsoup.parse(comments.mkString("\n\n")).text

  def writeToFile(basePath: Path): Try[Path] = Try {
    Files.write(basePath.resolve(s"$id.txt"), concatenated.getBytes(Charset.defaultCharset()))
  }

}
