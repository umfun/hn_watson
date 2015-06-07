package me.maciejb.hnanalysis.submission

import scala.collection.JavaConverters._

import org.jsoup.Jsoup

/**
 * @author Maciej Bilas
 * @since 6/12/14 12:24
 */
object WatsonRespExtractor {
  private val ProfileExtractor = """var theProfile = (\{.+\});""".r

  private def extractSnippetContainingTheProfile(htmlStr: String): Option[String] = {
    val elems = Jsoup.parse(htmlStr).
      select("div.container div.row div.col-lg-12 script").iterator().asScala.toList

    for (firstElem <- elems.headOption) yield firstElem.data()
  }

  def extractProfile(htmlStr: String): Option[String] = {
    for (snippet <- extractSnippetContainingTheProfile(htmlStr);
         profileDef = snippet.split("\n")(4);
         profileJson <- ProfileExtractor.unapplySeq(profileDef)
    ) yield profileJson.head
  }

  def profileOf(user: String, htmlStr: String): Option[String] =
    extractProfile(htmlStr) map {_.replaceFirst("\"id\":\"dummy\"", "\"id\":\"%s\"".format(user))}

}
