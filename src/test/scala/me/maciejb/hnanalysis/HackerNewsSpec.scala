package me.maciejb.hnanalysis

import org.scalatest.{Matchers, FlatSpec}

class HackerNewsSpec extends FlatSpec with Matchers {

  it should "have 100 leaders" in {
    HackerNews.leaders.length shouldEqual 100
  }

}
