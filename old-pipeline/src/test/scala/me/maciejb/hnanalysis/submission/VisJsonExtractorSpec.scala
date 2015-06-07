package me.maciejb.hnanalysis.submission

import com.softwaremill.thegarden.lawn.io.Resources
import org.scalatest.{FlatSpec, Matchers}

class VisJsonExtractorSpec extends FlatSpec with Matchers {

  val HipsumHtml = Resources.readToString("hipsum.html")

  it should "extract a data json" in {
     for (t <- WatsonRespExtractor.extractProfile(HipsumHtml)) {
       println(t)
     }
  }

}
