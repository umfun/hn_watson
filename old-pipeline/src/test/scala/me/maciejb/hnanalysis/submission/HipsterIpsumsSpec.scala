package me.maciejb.hnanalysis.submission


import me.maciejb.hnanalysis.samples.HipsterIpsums
import org.scalatest.{FlatSpec, Matchers}

class HipsterIpsumsSpec extends FlatSpec with Matchers {

  it should "read hipsum4.txt" in {
    val t = HipsterIpsums.Text4Paragraphs

    t shouldNot be(null)
    t.count(_ == '\n') shouldBe >(4)
  }

}
